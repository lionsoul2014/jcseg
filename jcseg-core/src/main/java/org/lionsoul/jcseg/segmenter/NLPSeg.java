package org.lionsoul.jcseg.segmenter;

import java.io.IOException;
import java.util.LinkedList;

import org.lionsoul.jcseg.IChunk;
import org.lionsoul.jcseg.ISegment;
import org.lionsoul.jcseg.IWord;
import org.lionsoul.jcseg.dic.ADictionary;
import org.lionsoul.jcseg.dic.ILexicon;
import org.lionsoul.jcseg.util.ArrayUtil;
import org.lionsoul.jcseg.util.ByteCharCounter;
import org.lionsoul.jcseg.util.EntityFormat;
import org.lionsoul.jcseg.util.IStringBuffer;
import org.lionsoul.jcseg.util.NumericUtil;
import org.lionsoul.jcseg.util.StringUtil;
import org.lionsoul.jcseg.util.TimeUtil;

/**
 * NLP segmentation implementation
 * And this extends all the properties of the Complex one
 * the rest of them are build for NLP only
 *
 * @author  chenxin<chenxin619315@gmail.com>
*/
public class NLPSeg extends ComplexSeg
{
	
	private static final long serialVersionUID = -8686944894332423915L;
	
	/**
     * word pool for NLP complex entity recognition
    */
    private final LinkedList<IWord> eWordPool = new LinkedList<IWord>();
    private final IStringBuffer buffer = new IStringBuffer(64);

    public NLPSeg(SegmenterConfig config, ADictionary dic)
    {
    	super(config, dic);
    	
        // basic common setting for NLP mode
        ///config.EN_SECOND_SEG   = false;
        ///config.CNFRA_TO_ARABIC = true;
        ///config.CNNUM_TO_ARABIC = true;
        config.APPEND_CJK_PINYIN = false;
        config.APPEND_CJK_SYN  = false;
        config.MAX_LATIN_LENGTH = 128;
    }

    /**
     * Override the next method to add the date-time entity recognition
     * And we also invoke the parent.next method to get the next token
     *
     * @see Segmenter#next()
    */
    public IWord next() throws IOException
    {
        if ( eWordPool.size() > 0 ) {
            return eWordPool.removeFirst();
        }

        final IWord word = super.next();
        if ( word == null ) {
            return null;
        }

        final String[] entity = word.getEntity();

        /*
         * This is a temporary program for
         * the_number or the_episode entity recognition.
         * ignore this if the entity is not null
         * added at 2017/05/25
        */
        if ( entity == null && word.getValue().charAt(0) == '第' ) {
            final IWord dWord = getNextTheWord(word);
            if ( dWord != null ) {
                dWord.setPartSpeechForNull(IWord.QUANTIFIER);
                return dWord;
            }
        }

        if ( entity == null ) {
            return word;
        }

        int eIdx = 0;
        if ( (eIdx = ArrayUtil.startsWith("time.a", entity)) > -1
        		|| (eIdx = ArrayUtil.startsWith(
        				Entity.E_DATETIME_P, entity)) > -1 ) {
            final IWord dWord = getNextTimeMergedWord(word, eIdx);
            if ( dWord != null ) {
                return dWord;
            }
        }

        if ( (eIdx = ArrayUtil.startsWith("datetime.ymd", entity)) > -1 ) {
            final IWord dWord = getNextDatetimeWord(word, eIdx);
            if ( dWord != null ) {
                return dWord;
            }
        }

        return word;
    }
    
    /**
     * internal interface to get the next word item buffered or not buffered
     * 
     * @return	IWord
    */
    private IWord _internalNext() throws IOException
    {
    	return eWordPool.size() > 0 ? eWordPool.removeFirst() : super.next();
    }

    /**
     * get the next the_xxx word like '第x个', '第x集' EG ...
     *
     * @param  word
     * @return IWord
    */
    protected IWord getNextTheWord(IWord word) throws IOException
    {
        String wVal = word.getValue();
        int wLen = word.getValue().length();
        IWord dWord = null;

        // the only 'the' word
        if ( wLen == 1 ) {
            return _nextNumberWord(word);
        }

        // template like '第三'
        if ( wLen == 2 ) {
            if ( NumericUtil.isCNNumeric(wVal.charAt(1)) == -1
                    && ! StringUtil.isEnNumeric(wVal.charAt(1)) ) {
                return null;
            }

            // update the word's entity
            word.addEntity(Entity.E_THE_NUMBER);
            return _nextNumberWord(word);
        }

        // template like '第x个'
        IWord unit = dic.get(ILexicon.NUMBER_UNIT, ""+wVal.charAt(wLen-1));
        if ( unit != null ) {
            if ( ! NumericUtil.isCNNumericString(wVal, 1, wLen-1)
                    && ! StringUtil.isDigit(wVal, 1, wLen-1) ) {
                return null;
            }

            dWord = word;
            dWord.setEntity(unit.getEntity());
            return dWord;
        }

        // the left template '第xxx'
        if ( ! NumericUtil.isCNNumericString(wVal, 1, wLen)
                && ! StringUtil.isDigit(wVal, 1, wLen) ) {
            return null;
        }

        word.addEntity(Entity.E_THE_NUMBER);
        return _nextNumberWord(word);
    }

    /**
     * child logic for {@link #getNextTheWord(IWord)}
     *
     * @param   word
     * @return  IWord
    */
    private IWord _nextNumberWord(IWord word) throws IOException
    {
        IWord dWord = null, unit = null;
        IWord dw1 = _internalNext();
        if ( dw1 == null ) {
            return null;
        }

        String[] w1Entity = dw1.getEntity();
        int w1Len = dw1.getValue().length();

        // pure numeric
        if ( ArrayUtil.indexOf(Entity.E_NUMERIC_ARABIC, w1Entity) > -1 ) {
            IWord dw2 = _internalNext();
            if ( dw2 == null ) {
                dWord = new Word(word.getValue()+dw1.getValue(), IWord.T_CJK_WORD);
                dWord.setPosition(word.getPosition());
                dWord.setEntity(Entity.E_THE_NUMBER_A);
                return dWord;
            }

            int w2Len = dw2.getValue().length();

            // template like '个', 'xx个'
            unit = dic.get(ILexicon.NUMBER_UNIT, ""+dw2.getValue().charAt(0));
            if ( unit != null ) {
                String w2Val = dw2.getValue();
                if ( w2Len == 1 || NumericUtil.isCNNumericString(w2Val, 0, w2Len-1)
                        || StringUtil.isDigit(w2Val, 0, w2Len-1) ) {
                    dWord = new Word(word.getValue()+dw1.getValue()+w2Val, IWord.T_CJK_WORD);
                    dWord.setPosition(word.getPosition());
                    dWord.setEntity(unit.getEntity());
                    return dWord;
                }
            }

            eWordPool.push(dw2);
            dWord = new Word(word.getValue()+dw1.getValue(), IWord.T_CJK_WORD);
            dWord.setPosition(word.getPosition());
            dWord.setEntity(Entity.E_THE_NUMBER_A);
            return dWord;
        }

        // template like '个', 'x个'
        unit = dic.get(ILexicon.NUMBER_UNIT, ""+dw1.getValue().charAt(w1Len-1));
        if ( unit != null ) {
            String w1Val = dw1.getValue();
            if ( w1Len == 1 || NumericUtil.isCNNumericString(w1Val, 0, w1Len-1)
                    || StringUtil.isDigit(w1Val, 0, w1Len-1) ) {
                dWord = new Word(word.getValue()+w1Val, IWord.T_CJK_WORD);
                dWord.setPosition(word.getPosition());
                dWord.setEntity(unit.getEntity());
                return dWord;
            }
        }

        eWordPool.push(dw1);
        return null;
    }


    /**
     * get and return the next time merged date-time word
     *
     * @param   word
     * @param   eIdx
     * @return  IWord
    */
    protected IWord getNextTimeMergedWord(IWord word, int eIdx) throws IOException
    {
        int pIdx = TimeUtil.getDateTimeIndex(word.getEntity(eIdx));
        if ( pIdx == TimeUtil.DATETIME_NONE ) {
            return null;
        }

        IWord[] wMask = TimeUtil.createDateTimePool();
        TimeUtil.fillDateTimePool(wMask, pIdx, word);

        IWord dWord = null;
        int mergedNum = 0;
        while ( (dWord = _internalNext()) != null ) {
            String[] entity = dWord.getEntity();
            if ( entity == null ) {
                eWordPool.push(dWord);
                break;
            }

            if ( ArrayUtil.startsWith("time.a", entity) > -1 ) {
            	if ( TimeUtil.DATETIME_NONE ==
                        TimeUtil.fillDateTimePool(wMask, dWord) ) {
                    eWordPool.push(dWord);
                    break;
                }
            } else if ( ArrayUtil.startsWith("datetime.hi", entity) > -1 ) {
                /*
                 * check and merge the datetime time part with a style
                 * like 15:45 or 15:45:36 eg...
                */
                TimeUtil.fillTimeToPool(wMask, dWord.getValue());
            } else if ( ArrayUtil.startsWith(Entity.E_DATETIME_P, entity) > -1 ) {
            	int tIdx = TimeUtil.fillDateTimePool(wMask, dWord);
            	if ( tIdx == TimeUtil.DATETIME_NONE || wMask[tIdx] == null ) {
                    eWordPool.push(dWord);
                    break;
                }
            } else {
                eWordPool.push(dWord);
                break;
            }

            mergedNum++;
        }

        if ( mergedNum == 0 ) {
            return null;
        }

        buffer.clear();
        for (IWord iWord : wMask) {
            if (iWord == null) {
                continue;
            }

            /* check and append the whitespace for the merged time word */
            if (buffer.length() > 0) {
                buffer.append(' ');
            }

            buffer.append(iWord.getValue());
        }

        dWord = new Word(buffer.toString(), IWord.T_BASIC_LATIN);
        dWord.setPosition(word.getPosition());
        dWord.setPartSpeechForNull(IWord.TIME_POSPEECH);

        //check and define the entity
        buffer.clear().append("datetime.");
        for (IWord iWord : wMask) {
            if (iWord == null) continue;
            buffer.append(TimeUtil.getTimeKey(iWord));
        }

        dWord.setEntity(new String[]{buffer.toString()});

        return dWord;
    }

    /**
     * get and return the next date-time word
     *
     * @param   word
     * @param   entityIdx
     * @return  IWord
    */
    protected IWord getNextDatetimeWord(IWord word, int entityIdx) throws IOException
    {
        IWord dWord = _internalNext();
        if ( dWord == null ) {
            return null;
        }
        
        String[] entity = dWord.getEntity();
        if ( entity == null ) {
            eWordPool.add(dWord);
            return null;
        }

        int eIdx = 0;
        if ( (eIdx = ArrayUtil.startsWith("datetime.h", entity)) > -1 ) {
            // do nothing here
        } else if ( (eIdx = ArrayUtil.startsWith("time.a", entity)) > -1
                || (eIdx = ArrayUtil.startsWith(Entity.E_DATETIME_P, entity)) > -1 ) {
            /*
             * @Note: added at 2017/04/01
             * 1, A word start with time.h or datetime.h could be merged
             * 2, if the new time merged word could not be merged with the origin word.
             *  and we should put the dWord to the first of the eWordPool CUZ #getNextTimeMergedWord
             * may append some IWord to the end of eWordPool
            */
            IWord mWord = getNextTimeMergedWord(dWord, eIdx);
            if ( mWord == null ) {
                eWordPool.addFirst(dWord);
                return null;
            }

            String mEntity = mWord.getEntity(0);
            if ( ! (mEntity.contains(".h") || mEntity.contains(".a")) ) {
                eWordPool.addFirst(mWord);
                return null;
            }

            eIdx   = 0;
            dWord  = mWord;
            entity = dWord.getEntity();
        } else {
            eWordPool.add(dWord);
            return null;
        }


        buffer.clear().append(word.getValue()).append(' ').append(dWord.getValue());
        dWord = new Word(buffer.toString(), IWord.T_BASIC_LATIN);
        dWord.setPosition(word.getPosition());
        dWord.setPartSpeechForNull(IWord.TIME_POSPEECH);

        //re-define the entity
        /// int sIdx = entity.indexOf('.') + 1;
        /// int sIdx = entity[eIdx].charAt(0) == 't' ? 5 : 9;
        int sIdx = 9;	// datetime
        buffer.clear().append(word.getEntity(0)).append(entity[eIdx].substring(sIdx));
        dWord.addEntity(buffer.toString());

        return dWord;
    }

    /**
     * internal method to define the composed entity
     * for numeric and unit word composed word
     *
     * @param	numeric
     * @param	unitWord
     * @return	IWord
    */
    private IWord getNumericUnitComposedWord(String numeric, IWord unitWord)
    {
    	final IStringBuffer sb = new IStringBuffer();
    	sb.clear().append(numeric).append(unitWord.getValue());
	    IWord wd = new Word(sb.toString(), IWord.T_CJK_WORD);

	    String[] entity = unitWord.getEntity();
	    int eIdx = ArrayUtil.startsWith(Entity.E_TIME_P, entity);
	    if ( eIdx > -1 ) {
	    	sb.clear().append(entity[eIdx].replace("time.", "datetime."));
	    } else {
	    	sb.clear().append(Entity.E_NUC_PREFIX ).append(unitWord.getEntity(0));
	    }

	    wd.setEntity(new String[] {sb.toString()});
	    wd.setPartSpeechForNull(IWord.QUANTIFIER);
	    sb.clear();

	    return wd;
    }

    public IWord getNumericUnitComposedWord(int numeric, IWord unitWord)
    {
    	return getNumericUnitComposedWord(String.valueOf(numeric), unitWord);
    }

    /**
     * @see Segmenter#getNextCJKWord(int, int)
    */
    @Override
    protected IWord getNextCJKWord(int c, int pos) throws IOException
    {
        char[] chars = nextCJKSentence(c);
        int cjkidx = 0;
        IWord w = null;
        while ( cjkidx < chars.length ) {
            //find the next CJK word.
            w = null;

            /*
             * check if there is Chinese numeric.
             * make sure chars[cjkidx] is a Chinese numeric
             * and it is not the last word.
            */
            int numVal = NumericUtil.isCNNumeric(chars[cjkidx]);
            if ( numVal > -1 ) {
                IWord unitWord = null;
                int wordLen = -1;
                String num = nextCNNumeric(chars, cjkidx);

                /*
                 * check the Chinese fraction.
                 * checkCF will be reset to be 'TRUE' it num is a Chinese fraction.
                 * @added 2013-12-14.
                */
                if ( (ctrlMask & ISegment.CHECK_CF_MASK) != 0  ) {
                    /*
                     * Here:
                     * Convert the Chinese fraction to Arabic fraction,
                     * if the Config.CNFRA_TO_ARABIC is true
                    */
                    if ( config.CNFRA_TO_ARABIC ) {
                        String[] split = num.split("分之");
                        w = new Word(
                            NumericUtil.cnNumericToArabic(split[1], true)+
                            "/"+NumericUtil.cnNumericToArabic(split[0], true),
                            IWord.T_CN_NUMERIC,
                            Entity.E_NUMERIC_FRACTION_A
                        );
                        w.setPartSpeechForNull(IWord.NUMERIC_POSPEECH);
                    } else {
                        w = new Word(num, IWord.T_CN_NUMERIC,
                        		Entity.E_NUMERIC_CN_FRACTION_A);
                        w.setPartSpeechForNull(IWord.NUMERIC_POSPEECH);
                    }
                } else {
                    String temp = null;
                    final IStringBuffer sb = new IStringBuffer();

                    /*
                     * check the Chinese numeric and the units
                     * try to find a Chinese and unit composed word
                    */
                    if ( numVal <= 10 ) {
                        for ( int j = num.length(), i = 0;
                                (cjkidx + j) < chars.length
                                && i < config.MAX_UNIT_LENGTH; j++, i++ ) {
                            sb.append(chars[cjkidx+j]);
                            temp = sb.toString();
                            if ( dic.match(ILexicon.CJK_UNIT, temp) ) {
                                unitWord = dic.get(ILexicon.CJK_UNIT, temp);
                            }
                        }
                    }

                    /*
                     * try to find the word that made up with the numeric
                     * like: "五四运动" or "五月天" eg ...
                     * Take the maximum matching word as the final token
                     * IIF the max matching word is longer than the unit word
                    */
                    IWord mmwd = null;
                    sb.clear().append(num);
                    for ( int j = num.length();
                            (cjkidx + j) < chars.length
                                && j < config.MAX_LENGTH; j++ ) {
                        sb.append(chars[cjkidx+j]);
                        temp = sb.toString();
                        if ( dic.match(ILexicon.CJK_WORD, temp) ) {
                            mmwd = dic.get(ILexicon.CJK_WORD, temp);
                        }
                    }

                    if ( mmwd == null ) {
                        if ( unitWord == null ) {
                            if ( config.CNNUM_TO_ARABIC ) {
                                w = new Word(String.valueOf(NumericUtil
                                		.cnNumericToArabic(num, true)),
                                		IWord.T_CN_NUMERIC);
                                w.setEntity(Entity.E_NUMERIC_ARABIC_A);
                                w.setPartSpeechForNull(IWord.NUMERIC_POSPEECH);
                                wordLen = num.length();
                            } else {
                                w = new Word(num, IWord.T_CN_NUMERIC);
                                w.setEntity(Entity.E_NUMERIC_CN_A);
                                w.setPartSpeechForNull(IWord.NUMERIC_POSPEECH);
                            }
                        } else {
                            if ( config.CNNUM_TO_ARABIC ) {
                                w = getNumericUnitComposedWord(NumericUtil
                                		.cnNumericToArabic(num, true), unitWord);
                                wordLen = num.length() + unitWord.getLength();
                            } else {
                                w = getNumericUnitComposedWord(num, unitWord);
                            }
                        }
                    } else {
                        if ( unitWord == null ) {
                            w = mmwd.clone();
                        } else if ( mmwd.getLength()
                        		> num.length() + unitWord.getLength() ) {
                            w = mmwd.clone();
                        } else if ( config.CNNUM_TO_ARABIC ) {
                            w = getNumericUnitComposedWord(NumericUtil
                            		.cnNumericToArabic(num, true), unitWord);
                            wordLen = num.length() + unitWord.getLength();
                        } else {
                        	w = getNumericUnitComposedWord(num, unitWord);
                        }
                    }
                }

                wordPool.add(w);
                w.setPosition(pos+cjkidx);
                cjkidx += wordLen > 0 ? wordLen : w.getLength();

                continue;
            }

            IChunk chunk = getBestChunk(chars, cjkidx, config.MAX_LENGTH);
            w = chunk.getWords()[0];

            /*
             * check and try to find a Chinese name.
            */
            int T = -1;
            if ( config.I_CN_NAME && w.getLength() <= 2
            		&& chunk.getWords().length > 1 ) {
                final StringBuilder sb = new StringBuilder();
                sb.append(w.getValue());
                String str = null;

                //the w is a Chinese last name.
                if ( dic.match(ILexicon.CN_LNAME, w.getValue())
                        && (str = findCHName(chars, 0, chunk)) != null) {
                    T = IWord.T_CN_NAME;
                    sb.append(str);
                }
                //the w is Chinese last name adorn
                else if ( dic.match(ILexicon.CN_LNAME_ADORN, w.getValue())
                        && chunk.getWords()[1].getLength() <= 2
                        && dic.match(ILexicon.CN_LNAME,
                                chunk.getWords()[1].getValue())) {
                    T = IWord.T_CN_NICKNAME;
                    sb.append(chunk.getWords()[1].getValue());
                }

                if ( T != -1 ) {
                    w = new Word(sb.toString(), T);
                    w.addEntity(T==IWord.T_CN_NICKNAME
                    		? Entity.E_NAME_NICKNAME : Entity.E_NAME_CN);
                    w.setPartSpeechForNull(IWord.NAME_POSPEECH);
                }
            }

            if ( config.CLEAR_STOPWORD
                    && dic.match(ILexicon.STOP_WORD, w.getValue()) ) {
                cjkidx += w.getLength();
                continue;
            }
            
            /*
             * reach the end of the chars - the last word.
             * check the existence of the Chinese and English mixed word
            */
            IWord ce = null;
            if ( (ctrlMask & ISegment.CHECK_CE_MASk) != 0
                    && (chars.length - cjkidx) <= dic.mixPrefixLength ) {
                ce = getNextMixedWord(chars, cjkidx);
            }
            
            /*
             * @Note: added at 2016/07/19
             * if the ce word is null and if the T is -1
             * the w should be a word that clone from itself
            */
            if ( ce == null ) {
                if ( T == -1 ) w = w.clone();
            } else {
                w = ce.clone();
            }

            w.setPosition(pos+cjkidx);
            wordPool.add(w);
            cjkidx += w.getLength();

            /*
             * check and append the Pinyin and the synonyms words.
            */
            if ( T == -1 ) {
            	appendCJKWordFeatures(w);
            }
        }

        if ( wordPool.size() == 0 ) {
            return null;
        }

        return wordPool.remove();
    }
    
    /**
     * @see Segmenter#enSecondSegFilter(IWord) 
    */
    protected boolean enSecondSegFilter(IWord w)
    {
    	return false;
    }

    /**
     * find the letter or digit word from the current position
     * count until the char is whitespace or not letter_digit
     *
     * @param  c
     * @param  pos
     * @return IWord
    */
    @Override
    protected IWord nextLatinWord(int c, int pos) throws IOException
    {
        isb.clear();
        if ( c > 65280 ) c -= 65248;
        if ( c >= 65 && c <= 90 ) c += 32;
        isb.append((char)c);

        boolean _check = false, _wspace = false;
        int ch, _ctype = 0, tcount = 1;             //number of different char type.
        int _TYPE  = StringUtil.getEnCharType(c);   //current char type.
        final ByteCharCounter counter = new ByteCharCounter();

        while ( (ch = readNext()) != -1 ) {
            // Covert the full-width char to half-width char.
            if ( ch > 65280 ) ch -= 65248;
            _ctype = StringUtil.getEnCharType(ch);
            if ( _ctype == StringUtil.EN_WHITESPACE ) {
                _wspace = true;
                break;
            }

            if ( _ctype == StringUtil.EN_PUNCTUATION ) {
                if ( ! StringUtil.isENKeepPunctuaton((char)ch) ) {
                    pushBack(ch);
                    break;
                }
                counter.increase((char)ch);
            }

            //Not EN_KNOW, and it could be letter, numeric.
            if ( _ctype == StringUtil.EN_UNKNOW ) {
                pushBack(ch);
                if ( StringUtil.isCJKChar( ch ) ) {
                    _check = true;
                }

                break;
            }

            //covert the lower case letter to upper case.
            if ( ch >= 65 && ch <= 90 ) ch += 32;
            if ( ch > 0 ) {
                isb.append((char)ch);
            }

            /* Char type counter.
             * condition to start the secondary segmentation.
             * @reader: we could do better.
             *
             * @added 2013-12-16
            */
            if ( _ctype != _TYPE ) {
                tcount++;
                _TYPE = _ctype;
            }

            if ( isb.length() > config.MAX_LATIN_LENGTH ) {
                break;
            }
        }

        /*
         * @added at 2016/11/24
         * clear the dot punctuation behind the string buffer
         * and recount the tcount as needed
        */
        int oLen = isb.length();
        for ( int i = oLen - 1; i > 0
                && StringUtil.isNoTailingPunctuation(isb.charAt(i)); i-- ) {
            pushBack(isb.charAt(i));
            isb.deleteCharAt(i);
            _check = false;
        }

        if ( oLen > isb.length()
                && ! StringUtil.isEnPunctuation(isb.last()) ) {
            tcount--;
        }



        IWord wd   = null;
        String str = isb.toString();
        String date = null;

        /*
         * special entity word check like email, URL, ip address
        */
        int colonNum = counter.get(':');
        int pointNum = counter.get('.');
        if ( counter.get('@') == 1 && pointNum > 0
                && EntityFormat.isMailAddress(str) ) {
            wd = new Word(str, IWord.T_BASIC_LATIN, Entity.E_EMAIL_A);
            wd.setPartSpeechForNull(IWord.EN_POSPEECH);
            return wd;
        } else if ( tcount == 1 && StringUtil.isEnNumeric(isb.first())
                && EntityFormat.isMobileNumber(str) ) {
            wd = new Word(str, IWord.T_BASIC_LATIN, Entity.E_MOBILE_A);
            wd.setPartSpeechForNull(IWord.NUMERIC_POSPEECH);
            return wd;
        } else if ( tcount == 7 && pointNum == 3
                && StringUtil.isEnNumeric(isb.first())
                && EntityFormat.isIpAddress(str) ) {
            wd = new Word(str, IWord.T_BASIC_LATIN, Entity.E_IP_A);
            wd.setPartSpeechForNull(IWord.EN_POSPEECH);
            return wd;
        } else if ( pointNum > 0 && colonNum == 1
                && EntityFormat.isUrlAddress(str, dic) ) {
            wd = new Word(str, IWord.T_BASIC_LATIN, Entity.E_URL_A);
            wd.setPartSpeechForNull(IWord.EN_POSPEECH);
            return wd;
        } else if ( pointNum == 2
                && (date = EntityFormat.isDate(str, '.')) != null ) {
            wd = new Word(date, IWord.T_BASIC_LATIN, Entity.E_DATETIME_YMD_A);
            wd.setPartSpeechForNull(IWord.TIME_POSPEECH);
            return wd;
        } else if ( counter.get('-') >= 1
                && (date = EntityFormat.isDate(str, '-')) != null ) {
            String[] entity = counter.get('-') == 1
                    ? Entity.E_DATETIME_YM_A : Entity.E_DATETIME_YMD_A;
            wd = new Word(date, IWord.T_BASIC_LATIN, entity);
            wd.setPartSpeechForNull(IWord.TIME_POSPEECH);
            return wd;
        } else if ( counter.get('/') >= 1
                && (date = EntityFormat.isDate(str, '/')) != null ) {
            String[] entity = counter.get('/') == 1
                    ? Entity.E_DATETIME_YM_A : Entity.E_DATETIME_YMD_A;
            wd = new Word(date, IWord.T_BASIC_LATIN, entity);
            wd.setPartSpeechForNull(IWord.TIME_POSPEECH);
            return wd;
        } else if ( (tcount == 3 || tcount == 5)
                && colonNum >= 1 && EntityFormat.isTime(str) ) {
            String[] entity = colonNum == 1
                    ? Entity.E_DATETIME_HI_A : Entity.E_DATETIME_HIS_A;
            wd = new Word(str, IWord.T_BASIC_LATIN, entity);
            wd.setPartSpeechForNull(IWord.TIME_POSPEECH);
            return wd;
        }

        /*
         * check the end condition.
         * and the check if the token loop was break by whitespace
         * cause there is no need to continue all the following work if it is.
        */
        if ( ch == -1 || _wspace ) {
            boolean isPercentage = false;
            for ( int i = isb.length() - 1; i > 0; i-- ) {
                if ( isb.charAt(i) == '%' ) {
                    if (StringUtil.isEnNumeric(isb.charAt(i-1))) {
                        isPercentage = true;
                        break;
                    }
                } else if ( ! StringUtil.isEnPunctuation(isb.charAt(i)) ) {
                    break;
                }

                /*
                 * try to find a English and punctuation mixed word.
                 * this will clear all the punctuation until a mixed word is found.
                 * like "i love c++.", c++ will be found from token "c++.".
                */
                if ( dic.match(ILexicon.CJK_WORD, str) ) {
                    wd = dic.get(ILexicon.CJK_WORD, str).clone();
                    break;
                }

                pushBack(isb.charAt(i));
                isb.deleteCharAt(i);
                str = isb.toString();
            }

            if ( wd == null ) {
                if ( isPercentage ) {
                    wd = new Word(str, IWord.T_BASIC_LATIN,
                    		Entity.E_NUMERIC_PERCENTAGE_A);
                    wd.setPartSpeechForNull(IWord.NUMERIC_POSPEECH);
                } else if ( tcount == 1 && StringUtil.isDigit(str) ) {
                    wd = new Word(str, IWord.T_BASIC_LATIN,
                    		Entity.E_NUMERIC_ARABIC_A);
                    wd.setPartSpeechForNull(IWord.NUMERIC_POSPEECH);
                } else if ( tcount == 3 && StringUtil.isDecimal(str) ) {
                    wd = new Word(str, IWord.T_BASIC_LATIN,
                    		Entity.E_NUMERIC_DECIMAL_A);
                    wd.setPartSpeechForNull(IWord.NUMERIC_POSPEECH);
                } else if ( dic.match(ILexicon.CJK_WORD, str) ) {
                    wd = dic.get(ILexicon.CJK_WORD, str).clone();
                } else {
                    wd = wordNewOrClone(ILexicon.CJK_WORD, str, IWord.T_BASIC_LATIN);
                    wd.setPartSpeechForNull(IWord.EN_POSPEECH);
                }
            }

            if ( wd.getPartSpeech() == null ) {
                wd.setPartSpeechForNull(IWord.EN_POSPEECH);
            }

            return wd;
        }

        /*
         * the loop was broken by an unknown char and it is not a CJK char
         * 1, check if the end char is a special single unit char like '℉,℃' and so on ..
         * 2, or do it as the end stream way like (ch == -1 or _wspace == true)
        */
        if (!_check) {
            /*
             * we check the units here, so we can recognize
             * many other units that is not Chinese like '℉,℃' and so on ...
            */
            boolean isDigit = StringUtil.isDigit(str);
            if ( isDigit || StringUtil.isDecimal(str) ) {
                ch = readNext();
                String unit = ((char)ch)+"";
                if ( dic.match(ILexicon.CJK_UNIT, unit) ) {
                    wd = getNumericUnitComposedWord(
                    		str, dic.get(ILexicon.CJK_UNIT, unit));
                } else {
                    String[] entity = isDigit
                    		? Entity.E_NUMERIC_ARABIC_A
                    				: Entity.E_NUMERIC_DECIMAL_A;
                    wd = new Word(str, IWord.T_BASIC_LATIN, entity);
                    wd.setPartSpeechForNull(IWord.NUMERIC_POSPEECH);
                    pushBack(ch);
                }
            }

            if ( wd == null ) {
                if ( dic.match(ILexicon.CJK_WORD, str) ) {
                    wd = dic.get(ILexicon.CJK_WORD, str).clone();
                } else {
                    wd = new Word(str, IWord.T_BASIC_LATIN);
                }
            }

            if ( wd.getPartSpeech() == null ) {
                wd.setPartSpeechForNull(IWord.EN_POSPEECH);
            }

            return wd;
        }

        /*
         * check and recognize the percentage
        */
        int length = isb.length();
        if ( length > 1 && isb.charAt(length-1) == '%'
                && StringUtil.isEnNumeric(isb.charAt(length-2)) ) {
            wd = new Word(str, IWord.T_BASIC_LATIN,
            		Entity.E_NUMERIC_PERCENTAGE_A);
            wd.setPartSpeechForNull(IWord.NUMERIC_POSPEECH);
            return wd;
        }

        /*
         * check and get English and Chinese mixed word like 'B超, x射线'
         *
         * Attention:
         * make sure that (ch = readNext()) is after j < Config.MIX_CN_LENGTH.
         * or it will cause the miss of the next char.
         *
         * @reader: (2013-09-25)
         * we do not check the type of the char read next.
         * so, words started with English and its length except the start English part
         * less than config.MIX_CN_LENGTH in the EC dictionary could be recognized.
         *
         * @Note added at 2017/08/05
         * Add the iBuffer.length checking logic to follow the limitation
         * of the maximum length of the current token
        */
        final IStringBuffer iBuffer = new IStringBuffer(str);
        String tstr = null;
        int mc = 0, j = 0;        //the number of char that read from the stream.
        iaList.clear();
        for ( ; j < dic.mixSuffixLength
                && iBuffer.length() < config.MAX_LENGTH
                    && (ch = readNext()) != -1; j++ ) {
            /*
             * Attention:
             * it is an accident that Jcseg works fine for
             * we break the loop directly when we meet a whitespace.
             * 1, if an EC word is found, unit check process will be ignored.
             * 2, if matches no EC word, certainly return of readNext()
             *   will make sure the units check process works fine.
            */
            if ( StringUtil.isWhitespace(ch) ) {
                pushBack(ch);
                break;
            }

            iBuffer.append((char)ch);
            iaList.add(ch);
            tstr = iBuffer.toString();
            if ( dic.match(ILexicon.CJK_WORD, tstr) ) {
                wd  = dic.get(ILexicon.CJK_WORD, tstr);
                mc = j + 1;
            }
        }

        iBuffer.clear();
        for ( int i = j - 1; i >= mc; i-- ) {
            pushBack(iaList.get(i));    //push back the read chars.
        }

        if ( wd != null ) {
            wd = wd.clone();
            if ( wd.getPartSpeech() == null ) {
                wd.setPartSpeechForNull(IWord.MIX_POSPEECH);
            }

            return wd;
        }

        /*
         * check the unit for the digit or the decimal Latin
        */
        boolean isDigit = StringUtil.isDigit(str);
        if ( isDigit || StringUtil.isDecimal(str) ) {
            iaList.clear();
            IWord unitWord = null;
            IStringBuffer sb = new IStringBuffer();
            for ( j = 0; j < config.MAX_UNIT_LENGTH
                    && (ch = readNext()) != -1; j++ ) {
                if ( StringUtil.isWhitespace(ch) ) {
                    pushBack(ch);
                    break;
                }

                sb.append((char)ch);
                iaList.add(ch);
                tstr = sb.toString();
                if ( dic.match(ILexicon.CJK_UNIT, tstr) ) {
                    unitWord = dic.get(ILexicon.CJK_UNIT, tstr);
                    mc = j + 1;
                }
            }

            if ( unitWord == null ) {
            	String[] entity = isDigit
                		? Entity.E_NUMERIC_ARABIC_A
                				: Entity.E_NUMERIC_DECIMAL_A;
                wd = new Word(str, IWord.T_BASIC_LATIN, entity);
                wd.setPartSpeechForNull(IWord.NUMERIC_POSPEECH);
            } else {
            	wd = getNumericUnitComposedWord(str, unitWord);
            }

            for ( int i = j - 1; i >= mc; i-- ) {
                pushBack(iaList.get(i));
            }
        }

        /*
         * simply return the combination of English char, Arabic
         * numeric, English punctuation if matches no single units or EC word.
        */
        if ( wd == null ) {
            if ( dic.match(ILexicon.CJK_WORD, str) ) {
                wd = dic.get(ILexicon.CJK_WORD, str).clone();
            } else {
                wd = new Word(str, IWord.T_BASIC_LATIN);
            }

            if ( wd.getPartSpeech() == null ) {
                wd.setPartSpeechForNull(IWord.EN_POSPEECH);
            }
        }

        return wd;
    }
    
}