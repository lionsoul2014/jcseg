package org.lionsoul.jcseg.tokenizer;

import java.io.IOException;
import java.io.Reader;

import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.Entity;
import org.lionsoul.jcseg.tokenizer.core.IChunk;
import org.lionsoul.jcseg.tokenizer.core.ILexicon;
import org.lionsoul.jcseg.tokenizer.core.ISegment;
import org.lionsoul.jcseg.tokenizer.core.IWord;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;
import org.lionsoul.jcseg.util.EntityFormat;
import org.lionsoul.jcseg.util.IStringBuffer;
import org.lionsoul.jcseg.util.NumericUtil;
import org.lionsoul.jcseg.util.StringUtil;

/**
 * NLP segmentation implementation
 * And this extends all the properties of the Complex one
 * the rest of them are build for NLP only
 * 
 * @author  chenxin<chenxin619315@gmail.com>
*/
public class NLPSeg extends ComplexSeg
{
    public NLPSeg(Reader input, JcsegTaskConfig config, ADictionary dic) throws IOException
    {
        super(input, config, dic);
        
        /*
         * basic common setting for NLP mode 
        */
        //config.EN_SECOND_SEG   = false;
        //config.CNFRA_TO_ARABIC = true;
        //config.CNNUM_TO_ARABIC = true;
        config.APPEND_CJK_PINYIN = false;
        config.APPEND_CJK_SYN  = false;
        config.MAX_LATIN_LENGTH = 128;
    }

    public NLPSeg(JcsegTaskConfig config, ADictionary dic) throws IOException
    {
        this(null, config, dic);
    }
    
    /**
     * @see ASegment#getNextCJKWord(int, int) 
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
                            Entity.E_NUMERIC_FRACTION
                        );
                        w.setPartSpeech(IWord.NUMERIC_POSPEECH);
                    } else {
                        w = new Word(num, IWord.T_CN_NUMERIC, Entity.E_NUMERIC_CN_FRACTION);
                        w.setPartSpeech(IWord.NUMERIC_POSPEECH);
                    }
                } else {
                    String temp = null;
                    IStringBuffer sb = new IStringBuffer();
                    
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
                     * like: "五四运动"
                    */
                    if ( unitWord == null ) {
                        IWord wd = null;
                        sb.clear().append(num);
                        for ( int j = num.length();
                                (cjkidx + j) < chars.length 
                                    && j < config.MAX_LENGTH; j++ ) {
                            sb.append(chars[cjkidx+j]);
                            temp = sb.toString();
                            if ( dic.match(ILexicon.CJK_WORD, temp) ) {
                                wd = dic.get(ILexicon.CJK_WORD, temp);
                            }
                        }
                        
                        if ( wd != null ) {
                            w = wd.clone();
                            wordLen = w.getLength();
                        } else if ( config.CNNUM_TO_ARABIC ) {
                            String arabic = NumericUtil.cnNumericToArabic(num, true)+"";
                            w = new Word(arabic, IWord.T_CN_NUMERIC, Entity.E_NUMERIC_ARABIC);
                            w.setPartSpeech(IWord.NUMERIC_POSPEECH);
                        } else {
                            w = new Word(num, IWord.T_CN_NUMERIC, Entity.E_NUMERIC_CN);
                            w.setPartSpeech(IWord.NUMERIC_POSPEECH);
                        }
                    } else if ( config.CNNUM_TO_ARABIC ) {
                        String arabic = NumericUtil.cnNumericToArabic(num, true)+"";
                        String entity = Entity.E_NUMERIC_ARABIC+"#"+unitWord.getEntity();
                        w = new Word(arabic, IWord.T_CN_NUMERIC, entity);
                        w.setPartSpeech(IWord.NUMERIC_POSPEECH);
                    } else {
                        String entity = Entity.E_NUMERIC_CN+"#"+unitWord.getEntity();
                        w = new Word(num, IWord.T_CJK_WORD, entity);
                        w.setPartSpeech(IWord.NUMERIC_POSPEECH);
                    }
                }
                
                wordPool.add(w);
                w.setPosition(pos+cjkidx);
                cjkidx += wordLen > 0 ? wordLen : num.length();
                
                if ( unitWord != null ) {
                    IWord wd = unitWord.clone();
                    wd.setPosition(pos+cjkidx);
                    wordPool.add(wd);
                    cjkidx += wd.getLength();
                }
                
                continue;
            }
            
            
            IChunk chunk = getBestCJKChunk(chars, cjkidx);
            w = chunk.getWords()[0];
            
            /* 
             * check and try to find a Chinese name.
            */
            int T = -1;
            if ( config.I_CN_NAME && w.getLength() <= 2 && chunk.getWords().length > 1 ) {
                StringBuilder sb = new StringBuilder();
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
                    w.setEntity(T==IWord.T_CN_NICKNAME ? Entity.E_NAME_NICKNAME : Entity.E_NAME_CN);
                    w.setPartSpeech(IWord.NAME_POSPEECH);
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
                    && (chars.length - cjkidx) <= config.MIX_PREFIX_LENGTH ) {
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
                appendWordFeatures(w);
            }
        }
        
        if ( wordPool.size() == 0 ) {
            return null;
        }
        
        return wordPool.remove();
    }

    /**
     * find the letter or digit word from the current position 
     * count until the char is whitespace or not letter_digit
     * 
     * @param  c
     * @param  pos
     * @return IWord
     * @throws IOException 
    */
    @Override
    protected IWord nextLatinWord(int c, int pos) throws IOException 
    {
        isb.clear();
        if ( c > 65280 ) c -= 65248;
        if ( c >= 65 && c <= 90 ) c += 32; 
        isb.append((char)c);
        
        boolean _check = false, _wspace = false;
        int atcount = 0, ptcount = 0;
        int ch, _ctype = 0, tcount = 1;             //number of different char type.
        int _TYPE  = StringUtil.getEnCharType(c);   //current char type.
        
        while ( (ch = readNext()) != -1 ) {
            //Covert the full-width char to half-width char.
            if ( ch > 65280 ) ch -= 65248;
            _ctype = StringUtil.getEnCharType(ch);
            if ( _ctype == StringUtil.EN_WHITESPACE ) {
                _wspace = true;
                break;
            }
            
            if ( _ctype == StringUtil.EN_PUNCTUATION ) {
                if ( ch == '@' ) {
                    atcount++;
                } else if ( ch == '.' ) {
                    ptcount++;
                } else if ( ch == ':' ) {
                    int nchr1 = readNext(), nchr2 = readNext();
                    if ( nchr1 == '/' && nchr2 == '/' ) {
                        isb.append((char)ch)
                            .append((char)nchr1)
                                .append((char)nchr2);
                        ch = -1;
                    } else {
                        pushBack(nchr2);
                        pushBack(nchr1);
                        pushBack(ch);
                        break;
                    }
                } else if ( ! StringUtil.isENKeepPunctuaton((char)ch) ) {
                    pushBack(ch);
                    break;
                }
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
        for ( int i = oLen - 1; i > 0 && isb.charAt(i) == '.'; i-- ) {
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
        
        /*
         * special entity word check like email, url address
        */
        if ( atcount == 1 && EntityFormat.isMailAddress(str) ) {
            wd = new Word(str, IWord.T_BASIC_LATIN, Entity.E_EMAIL);
            wd.setPartSpeech(IWord.EN_POSPEECH);
            return wd;
        } else if ( tcount == 1 && StringUtil.isEnNumeric(isb.first()) 
                && EntityFormat.isMobileNumber(str) ) {
            wd = new Word(str, IWord.T_BASIC_LATIN, Entity.E_MOBILE_NUMBER);
            wd.setPartSpeech(IWord.NUMERIC_POSPEECH);
            return wd;
        } else if ( tcount == 7 && StringUtil.isEnNumeric(isb.first()) 
                && EntityFormat.isIpAddress(str) ) {
            wd = new Word(str, IWord.T_BASIC_LATIN, Entity.E_IP);
            wd.setPartSpeech(IWord.EN_POSPEECH);
            return wd;
        } else if ( ptcount > 0 && EntityFormat.isUrlAddress(str, dic) ) {
            wd = new Word(str, IWord.T_BASIC_LATIN, Entity.E_URL);
            wd.setPartSpeech(IWord.EN_POSPEECH);
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
                    if ( i > 0 && StringUtil.isEnNumeric(isb.charAt(i-1)) ) {
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
                    wd = new Word(str, IWord.T_BASIC_LATIN, Entity.E_NUMERIC_PERCENTAGE);
                    wd.setPartSpeech(IWord.NUMERIC_POSPEECH);
                } else if ( tcount == 1 && StringUtil.isDigit(str) ) {
                    wd = new Word(str, IWord.T_BASIC_LATIN, Entity.E_NUMERIC_ARABIC);
                    wd.setPartSpeech(IWord.NUMERIC_POSPEECH);
                } else if ( tcount == 3 && StringUtil.isDecimal(str) ) {
                    wd = new Word(str, IWord.T_BASIC_LATIN, Entity.E_NUMERIC_DECIMAL);
                    wd.setPartSpeech(IWord.NUMERIC_POSPEECH);
                } else if ( dic.match(ILexicon.CJK_WORD, str) ) {
                    wd = dic.get(ILexicon.CJK_WORD, str).clone();
                } else {
                    wd = new Word(str, IWord.T_BASIC_LATIN);
                    wd.setPartSpeech(IWord.EN_POSPEECH);
                }
            }
            
            if ( wd.getPartSpeech() == null ) {
                wd.setPartSpeech(IWord.EN_POSPEECH);
            }
            
            return wd;
        }
        
        /*
         * the loop was broke by an unknown char and it is not a CJK char
         * 1, check if the end char is a special single unit char like '℉,℃' and so on ..
         * 2, or do it as the end stream way like (ch == -1 or _wspace == true) 
        */
        if ( _check == false ) {
            /* 
             * we check the units here, so we can recognize
             * many other units that is not Chinese like '℉,℃' and so on ... 
            */
            boolean isDigit = StringUtil.isDigit(str);
            if ( isDigit || StringUtil.isDecimal(str) ) {
                String entity = isDigit ? Entity.E_NUMERIC_ARABIC : Entity.E_NUMERIC_DECIMAL;
                wd = new Word(str, IWord.T_BASIC_LATIN, entity);
                wd.setPartSpeech(IWord.NUMERIC_POSPEECH);
                
                ch = readNext();
                String unit = ((char)ch)+"";
                if ( dic.match(ILexicon.CJK_UNIT, unit) ) {
                    IWord unitWord = dic.get(ILexicon.CJK_UNIT, unit).clone();
                    unitWord.setPosition(pos+str.length());
                    wordPool.add(unitWord);
                } else {
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
                wd.setPartSpeech(IWord.EN_POSPEECH);
            }
            
            return wd;
        }
        
        /*
         * check and recognize the percentage 
        */
        int length = isb.length();
        if ( length > 1 && isb.charAt(length-1) == '%' 
                && StringUtil.isEnNumeric(isb.charAt(length-2)) ) {
            wd = new Word(str, IWord.T_BASIC_LATIN, Entity.E_NUMERIC_PERCENTAGE);
            wd.setPartSpeech(IWord.NUMERIC_POSPEECH);
            return wd;
        }
        
        /* 
         * check and get English and Chinese mixed word like 'B超, x射线'
         * 
         * Attention:
         * make sure that (ch = readNext()) is after j < Config.MIX_CN_LENGTH.
         * or it cause the miss of the next char. 
         * 
         * @reader: (2013-09-25)
         * we do not check the type of the char read next.
         * so, words started with English and its length except the start English part
         * less than config.MIX_CN_LENGTH in the EC dictionary could be recognized.
        */
        IStringBuffer ibuffer = new IStringBuffer(str);
        String tstr = null;
        int mc = 0, j = 0;        //the number of char that read from the stream.
        ialist.clear();
        for ( ; j < config.MIX_SUFFIX_LENGTH && (ch = readNext()) != -1; j++ ) {
            /* 
             * Attention:
             * it is a accident that Jcseg works fine for 
             * we break the loop directly when we meet a whitespace.
             * 1, if a EC word is found, unit check process will be ignore.
             * 2, if matches no EC word, certainly return of readNext() 
             *   will make sure the units check process works fine.
            */
            if ( StringUtil.isWhitespace(ch) ) {
                pushBack(ch);
                break;
            }
            
            ibuffer.append((char)ch);
            ialist.add(ch);
            tstr = ibuffer.toString();
            if ( dic.match(ILexicon.CJK_WORD, tstr) ) {
                wd  = dic.get(ILexicon.CJK_WORD, tstr);
                mc = j + 1;
            }
        }
        
        ibuffer.clear();
        ibuffer = null;                 //Let gc do it's work.
        for ( int i = j - 1; i >= mc; i-- ) {
            pushBack(ialist.get(i));    //push back the read chars.
        }

        if ( wd != null ) {
            wd = wd.clone();
            if ( wd.getPartSpeech() == null ) {
                wd.setPartSpeech(IWord.MIX_POSPEECH);
            }
            
            return wd;
        }
        
        /* 
         * check the unit for the digit or the decimal Latin
        */
        boolean isDigit = StringUtil.isDigit(str);
        if ( isDigit || StringUtil.isDecimal(str) ) {
            String entity = isDigit ? Entity.E_NUMERIC_ARABIC : Entity.E_NUMERIC_DECIMAL;
            wd = new Word(str, IWord.T_BASIC_LATIN, entity);
            wd.setPartSpeech(IWord.NUMERIC_POSPEECH);
            
            ialist.clear();
            IWord unitWord = null;
            IStringBuffer sb = new IStringBuffer();
            for ( j = 0; j < config.MAX_UNIT_LENGTH 
                    && (ch = readNext()) != -1; j++ ) {
                if ( StringUtil.isWhitespace(ch) ) {
                    pushBack(ch);
                    break;
                }
                
                sb.append((char)ch);
                ialist.add(ch);
                tstr = isb.toString();
                if ( dic.match(ILexicon.CJK_UNIT, tstr) ) {
                    unitWord = dic.get(ILexicon.CJK_UNIT, tstr);
                    mc = j + 1;
                }
            }
            
            if ( unitWord != null ) {
                unitWord = unitWord.clone();
                unitWord.setPosition(pos+str.length());
                wordPool.add(unitWord);
            }
            
            for ( int i = j - 1; i >= mc; i-- ) {
                pushBack(ialist.get(i));
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
                wd.setPartSpeech(IWord.EN_POSPEECH);
            }
        }
        
        return wd;
    }
    
}
