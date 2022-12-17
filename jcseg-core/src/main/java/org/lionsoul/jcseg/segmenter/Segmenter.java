package org.lionsoul.jcseg.segmenter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.lionsoul.jcseg.util.NumericUtil;
import org.lionsoul.jcseg.util.StringUtil;
import org.lionsoul.jcseg.IChunk;
import org.lionsoul.jcseg.ISegment;
import org.lionsoul.jcseg.IWord;
import org.lionsoul.jcseg.dic.ADictionary;
import org.lionsoul.jcseg.dic.ILexicon;
import org.lionsoul.jcseg.util.IPushbackReader;
import org.lionsoul.jcseg.util.IStringBuffer;
import org.lionsoul.jcseg.util.IntArrayList;

/**
 * abstract segmentation super class:
 * 1. implemented the ISegment interface 
 * 2. implemented all the common functions
 * that simple, complex, most segmentation algorithm will all share.
 * 
 * @author  chenxin<chenxin619315@gmail.com>
*/
public abstract class Segmenter implements ISegment 
{
    /**
     * the index value of the current input stream
     * mainly for track the start position of the token
    */
    protected int idx;
    
    //protected PushbackReader reader = null;
    protected IPushbackReader reader = null;
    
    /**
     * CJK word cache pool, Reusable string buffer
     * and the array list for basic integer
    */
    final protected LinkedList<IWord> wordPool;
    final protected LinkedList<IWord> subWordPool;
    //protected IHashQueue<IWord> wordPool = null;
    final protected IStringBuffer isb;
    final protected IntArrayList iaList;
    
    /**
     * global behind Latin word after the CJK word
     * added at 2016/11/22 for better mixed word implementation 
    */
    protected String behindLatin = null;
    
    /**
     * segmentation runtime function control mask
    */
    protected int ctrlMask = 0;
    
    /**
     * the dictionary and task configuration instance
    */
    public final ADictionary dic;
    public final SegmenterConfig config;
    
    /**
     * initialize the segment
     * 
     * @param   config Jcseg task configuration instance
     * @param   dic Jcseg dictionary instance
    */
    public Segmenter(SegmenterConfig config, ADictionary dic )
    {
        this.config = config;
        this.dic    = dic;
        wordPool    = new LinkedList<>();
        subWordPool = new LinkedList<>();
        isb         = new IStringBuffer(64);
        iaList = new IntArrayList(15);
    }
    
    /**
     * input stream and reader reset.
     * 
     * @param input
     */
    public void reset( Reader input ) throws IOException
    {
        if ( input != null ) {
            reader = new IPushbackReader(new BufferedReader(input));
        }
        
        idx = -1;
    }
    
    /**
     * read the next char from the current position
     */
    protected int readNext() throws IOException 
    {
        int c = reader.read();
        if ( c != -1 ) idx++;
        return c;
    }
    
    /**
     * push back the data to the stream.
     * 
     * @param   data
     */
    protected void pushBack( int data )
    {
        reader.unread(data);
        idx--;
    }
    
    /**
     * push back a string to the stream
     * 
     * @param   str
    */
    protected void pushBack(String str)
    {
        char[] chars = str.toCharArray();
        for ( int j = chars.length - 1; j >= 0; j-- ) {
            reader.unread(chars[j]);
        }
        idx -= chars.length;
    }
    
    @Override
    public int getStreamPosition() 
    {
        return idx + 1;
    }
    
    /**
     * get the current dictionary instance.
     * 
     * @return    ADictionary
     */
    public ADictionary getDict() 
    {
        return dic;
    }
    
    /**
     * get the current task configuration instance.
     */
    public SegmenterConfig getConfig() 
    {
        return config;
    }

    /**
     * @see ISegment#next() 
     */
    @Override
    public IWord next() throws IOException 
    {
        /*
         * @Note: check and get the token directly from the word pool
         * if word pool is available.
         * changed wordPool to IHashQueue for the same Word in wordPool
         * the start position of the word will be the last one
         * 
         * @added: 2014-04-11
         */
        if ( wordPool.size() > 0 ) {
            return wordPool.remove();
        }
        
        int c, pos;
        IWord word = null;
        while ( (c = readNext()) != -1 ) {
            if ( StringUtil.isWhitespace(c) ) continue;
            pos = idx;
            
            /* CJK char.
             * and start the CJK word analysis
            */
            if ( StringUtil.isCJKChar( c ) ) {
                behindLatin = null;
                word = getNextCJKWord(c, pos);
                if ( behindLatin != null ) {
                    pushBack(behindLatin);
                }
            }
            /* English/Latin char.
             * and start the Latin word analysis
            */
            else if ( StringUtil.isEnChar(c) ) {
                word = getNextLatinWord(c, pos);
            }
            /* find a content around with pair punctuation.
             * set the config.pptmaxlen to 0 to close it
            */
            else if ( config.PPT_MAX_LENGTH > 0 
                    && StringUtil.isPairPunctuation( (char)c ) ) {
                word = getNextPunctuationPairWord(c, pos);
            } 
            /* letter number like 'ⅠⅡ';
             */
            else if ( StringUtil.isLetterNumber(c) ) {
                String val = nextLetterNumber(c);
                if ( config.CLEAR_STOPWORD 
                        && dic.match(ILexicon.STOP_WORD, val) ) {
                    continue;
                }
                
                word = new Word(val, IWord.T_OTHER_NUMBER);
                word.setPartSpeechForNull(IWord.NUMERIC_POSPEECH);
                word.setPosition(pos);
            }
            /* other number like '①⑩⑽㈩';
             */
            else if ( StringUtil.isOtherNumber(c) ) {
                String val = nextOtherNumber(c);
                if ( config.CLEAR_STOPWORD 
                        && dic.match(ILexicon.STOP_WORD, val) ) {
                    continue;
                }
                
                word = new Word(val, IWord.T_OTHER_NUMBER);
                word.setPartSpeechForNull(IWord.NUMERIC_POSPEECH);
                word.setPosition(pos);
            } 
            /* Chinese punctuation.
             */
            else if ( StringUtil.isCnPunctuation( c ) ) {
                String str = String.valueOf((char)c);
                if ( config.CLEAR_STOPWORD 
                        && dic.match(ILexicon.STOP_WORD, str)) {
                    continue;
                }
                
                word = new Word(str, IWord.T_PUNCTUATION);
                word.setPartSpeechForNull(IWord.PUNCTUATION);
                word.setPosition(pos);
            }
            /* @reader: (2013-09-25) 
             * unrecognized char will cause unknown problem for different system.
             * keep it or clear it ?
             * if you use Jcseg for search, better close it.
            */
            else if ( config.KEEP_UNREG_WORDS ) {
                String str = String.valueOf((char)c);
                if ( config.CLEAR_STOPWORD
                        && dic.match(ILexicon.STOP_WORD, str)) {
                    continue;
                }
                
                word = new Word(str, IWord.T_UNRECOGNIZE_WORD);
                word.setPartSpeechForNull(IWord.UNRECOGNIZE);
                word.setPosition(pos);
            }
            
            if ( word != null ) {
                return word;
            }
            
            /*
             * The variable word could be null
             * and that means the analysis logic reached a stop word
             * here we got to continue the loop and let it go
             * 
             * @Added at 2016/08/21
             * if the word is null we should check the wordPool first
             * or if the #continue lead the next loop to the end of the stream
             * then the buffered word will miss
            */
            if ( wordPool.size() > 0 ) {
                return wordPool.removeFirst();
            }
        }
        
        return null;
    }
    
    /**
     * get the next CJK word from the current position of the input stream
     * 
     * @param   c
     * @param   pos
     * @return  IWord could be null and that mean we reached a stop word
    */
    protected IWord getNextCJKWord(int c, int pos) throws IOException
    {
        char[] chars = nextCJKSentence(c);
        int cjkidx = 0;
        IWord w = null;
        while ( cjkidx < chars.length ) {
            /*
             * find the next CJK word.
             * the process will be different for the different implementation algorithm
             * @see getBestCJKChunk() from SimpleSeg or ComplexSeg. 
             */
            w = null;
            
            
            /*
             * check if there is Chinese numeric. 
             * make sure chars[cjkidx] is a Chinese numeric
             * and it is not the last word.
            */
            if ( cjkidx + 1 < chars.length 
                    && NumericUtil.isCNNumeric(chars[cjkidx]) > -1 ) {
                //get the Chinese numeric chars
                String num = nextCNNumeric( chars, cjkidx );
                int NUMLEN = num.length();
                
                /*
                 * check the Chinese fraction.
                 * old logic: {{{
                 * cjkidx + 3 < chars.length && chars[cjkidx+1] == '分' 
                 *         && chars[cjkidx+2] == '之' 
                 * && CNNMFilter.isCNNumeric(chars[cjkidx+3]) > -1.
                 * }}}
                 * 
                 * checkCF will be reset to be 'TRUE' it num is a Chinese fraction.
                 * @added 2013-12-14.
                 * */
                if ( (ctrlMask & ISegment.CHECK_CF_MASK) != 0  ) {
                    w = new Word(num, IWord.T_CN_NUMERIC);
                    w.setPosition(pos+cjkidx);
                    w.setPartSpeechForNull(IWord.NUMERIC_POSPEECH);
                    wordPool.add(w);
                    
                    /* 
                     * Here: 
                     * Convert the Chinese fraction to Arabic fraction,
                     * if the Config.CNFRA_TO_ARABIC is true.
                     */
                    if ( config.CNFRA_TO_ARABIC ) {
                        String[] split = num.split("分之");
                        IWord wd = new Word(
                            NumericUtil.cnNumericToArabic(split[1], true)+
                            "/"+NumericUtil.cnNumericToArabic(split[0], true),
                            IWord.T_CN_NUMERIC
                        );
                        wd.setPosition(w.getPosition());
                        wd.setPartSpeechForNull(IWord.NUMERIC_POSPEECH);
                        wordPool.add(wd);
                    }
                }
                /*
                 * check the Chinese numeric and single units.
                 * type to find Chinese and unit composed word.
                */
                else if ( NumericUtil.isCNNumeric(chars[cjkidx+1]) > -1
                        || dic.match(ILexicon.CJK_UNIT, chars[cjkidx+1]+"") ) {
                    StringBuilder sb = new StringBuilder();
                    String temp      = null;
                    String ONUM      = num;    //backup the old numeric
                    sb.append(num);
                    boolean matched = false;
                    int j;
                    
                    /*
                     * find the word that made up with the numeric
                     * like: "五四运动"
                    */
                    for ( j = num.length();
                            (cjkidx + j) < chars.length 
                                && j < config.MAX_LENGTH; j++ ) {
                        sb.append(chars[cjkidx + j]);
                        temp = sb.toString();
                        if ( dic.match(ILexicon.CJK_WORD, temp) ) {
                            w = dic.get(ILexicon.CJK_WORD, temp);
                            num = temp;
                            matched = true;
                        }
                    }
                    
                    /*
                     * @Note: when matched is true, num maybe a word like '五月',
                     * yat, this will make it skip the Chinese numeric to Arabic logic
                     * so find the matched word that it maybe a single Chinese units word
                     * 
                     * @added: 2014-06-06
                     */
                    if ( matched && num.length() - NUMLEN == 1
                            && dic.match(ILexicon.CJK_UNIT, num.substring(NUMLEN)) ) {
                        num     = ONUM;
                        matched = false;    //reset the matched
                    }
                    
                    IWord wd = null;
                    if ( !matched && config.CNNUM_TO_ARABIC ) {
                        String arabic = NumericUtil.cnNumericToArabic(num, true)+"";
                        if ( (cjkidx + num.length()) < chars.length
                                && dic.match(ILexicon.CJK_UNIT, chars[cjkidx + num.length()]+"") ) {
                            char units = chars[ cjkidx + num.length() ];
                            num += units; arabic += units;
                        }
                        
                        wd = new Word(arabic, IWord.T_CN_NUMERIC);
                        wd.setPosition(pos+cjkidx);
                        wd.setLength(num.length());
                        wd.setPartSpeechForNull(IWord.NUMERIC_POSPEECH);
                    }
                    
                    // clear the stop words as need
                    if ( config.CLEAR_STOPWORD 
                            && dic.match(ILexicon.STOP_WORD, num) ) {
                        cjkidx += num.length();
                        continue;
                    }
                    
                    /*
                     * @Note: added at 2016/07/19
                     * we cannot share the position with the original word item in the
                     * global dictionary accessed with this.dic
                     * 
                     * CUZ under the concurrency ENV that will lead to the position error
                     * so, we clone it if the word is directly get from the dictionary
                    */
                    if ( w == null ) {
                        w = new Word(num, IWord.T_CN_NUMERIC);
                        w.setPartSpeechForNull(IWord.NUMERIC_POSPEECH);
                    } else {
                        w = w.clone();
                    }
                    
                    w.setPosition(pos + cjkidx);
                    wordPool.add(w);
                    if ( wd != null ) {
                        wordPool.add(wd);
                    }
                }
                
                if ( w != null ) {
                    cjkidx += w.getLength();
                    appendCJKWordFeatures(w);
                    continue;
                }
            }
            
            
            final IChunk chunk = getBestChunk(chars, cjkidx, config.MAX_LENGTH);
            w = chunk.getWords()[0];
            String wps = w.getPartSpeech()==null ? null : w.getPartSpeech()[0];
            
            /* 
             * check and try to find a Chinese name.
             * 
             * @Note at 2017/05/19
             * add the origin part of speech check, if the
             * w is a Chinese name already and just let it go
            */
            int T = -1;
            if ( config.I_CN_NAME && (!"nr".equals(wps))
                    && w.getLength() <= 2 && chunk.getWords().length > 1  ) {
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
                /*
                 * the length of the w is 2:
                 * the last name and the first char make up a word
                 * for the double name. 
                 */
                /// else if ( w.getLength() > 1
                ///         && findCHName( w, chunk ))  {
                ///     T = IWord.T_CN_NAME;
                ///     sb.append(chunk.getWords()[1].getValue().charAt(0));
                /// }
                
                if ( T != -1 ) {
                    w = new Word(sb.toString(), T);
                    w.setPartSpeechForNull(IWord.NAME_POSPEECH);
                }
            }
            
            //check and clear the stop words
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
                if ( ce != null ) {
                    T = -1;
                }
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
     * get the next Latin word from the current position of the input stream
     * 
     * @param   c
     * @param   pos
     * @return  IWord could be null and that mean we reached a stop word
    */
    protected IWord getNextLatinWord(int c, int pos) throws IOException
    {
        /*
         * clear or just return the English punctuation as
         * a single word with PUNCTUATION type and part of speech 
        */
        if ( StringUtil.isEnPunctuation( c ) ) {
            String str = String.valueOf((char)c);
            if ( config.CLEAR_STOPWORD 
                    && dic.match(ILexicon.STOP_WORD, str) ) {
                return null;
            }
            
            IWord w = new Word(str, IWord.T_PUNCTUATION);
            w.setPosition(pos);
            w.setPartSpeechForNull(IWord.PUNCTUATION);
            return w;
        }
        
        IWord w = nextLatinWord(c, pos);
        w.setPosition(pos);
        
        /* if it is a stop word, here we stop everything here */
        if ( config.CLEAR_STOPWORD && dic.match(ILexicon.STOP_WORD, w.getValue()) ) {
        	w = null;	// Let gc do its work
        	return null;
        }
        
        /* We stop here if there is no necessary 
         * to do the char type or lexicon word segmentation */
		if ( !config.EN_SECOND_SEG || !enSecondSegFilter(w)) {
        	appendCJKWordFeatures(w);
        	return w;
        }
        
    	/* @added: 2013-12-16
         * check and do the secondary segmentation work.
         * This will split 'qq2013' to 'qq, 2013'.
        */
        subWordPool.clear();
        if ( (ctrlMask & ISegment.START_SS_MASK) != 0 ) {
        	enSecondSeg(w, subWordPool);
        } else if ( config.EN_WORD_SEG ) {
        	enWordSeg(w, subWordPool);
        } else {
        	appendCJKWordFeatures(w);
        	return w;
        }
        
        /* Adjust the sub word token and process for the output 
         * The first sub word token should be put ahead of the current word token 
         * if its position is the same with the current parent word token.
        */
        if ( subWordPool.isEmpty() ) {
            appendLatinWordFeatures(w);
            return w;
        }
        
        /* @Note: we will ignore the current word 'w' and let
         * the child logic do make the choice whether to keep it or not.
         * append the word to the word list buffer to keep it or ignore it. */
    	w = subWordPool.removeFirst();
    	appendLatinWordFeatures(w);
    	
    	/* append all the rest of sub word tokens and 
    	 * their features to the global word pool */
    	for ( final IWord sw : subWordPool ) {
    		wordPool.add(sw);
    		appendLatinWordFeatures(sw);
    	}

        return w;
    }
    
    /**
     * get the next mixed word, CJK-English or CJK-English-CJK or whatever
     * 
     * @param   chars
     * @param   cjkidx
     * @return  IWord or null for nothing found
    */
    protected IWord getNextMixedWord(char[] chars, int cjkidx) throws IOException
    {
        final IStringBuffer buff = new IStringBuffer();
        buff.clear().append(chars, cjkidx);
        String tString = buff.toString();
        if ( ! dic.match(ILexicon.MIX_ASSIST_WORD, tString) ) {
            return null;
        }
        
        /*
         * check and append the behind Latin string 
        */
        if ( behindLatin == null ) {
            behindLatin = nextLatinString(readNext());
        }
        
        IWord wd = null;
        buff.append(behindLatin);
        tString = buff.toString();
        if ( dic.match(ILexicon.CJK_WORD, tString) ) {
            wd = dic.get(ILexicon.CJK_WORD, tString);
        }
        
        if ( (ctrlMask & ISegment.CHECK_EC_MASK) != 0 
                || dic.match(ILexicon.MIX_ASSIST_WORD, tString) ) {
            iaList.clear();
            int chr = -1, j, mc = 0;
            for ( j = 0; j < dic.mixSuffixLength && (chr = readNext()) != -1; j++ ) {
                buff.append((char)chr);
                iaList.add(chr);
                tString = buff.toString();
                if ( dic.match(ILexicon.CJK_WORD, tString) ) {
                    wd = dic.get(ILexicon.CJK_WORD, tString);
                    mc = j + 1;
                }
            }
            
            //push back the read chars.
            for ( int i = j - 1; i >= mc; i-- ) {
                pushBack(iaList.get(i));
            }
        }
        
        buff.clear();
        if ( wd != null ) {
            behindLatin = null;
        }
        
        return wd;
    }
    
    /**
     * get the next punctuation pair word from the current position 
     * of the input stream.
     * 
     * @param   c
     * @param   pos
     * @return  IWord could be null and that mean we reached a stop word
    */
    protected IWord getNextPunctuationPairWord(int c, int pos) throws IOException
    {
        IWord w = null, w2 = null;
        String text = getPairPunctuationText(c);
        
        //handle the punctuation.
        String str = String.valueOf((char)c);
        if ( ! ( config.CLEAR_STOPWORD 
                && dic.match(ILexicon.STOP_WORD, str) ) ) {
            w = new Word(str, IWord.T_PUNCTUATION);
            w.setPartSpeechForNull(IWord.PUNCTUATION);
            w.setPosition(pos);
        }
        
        //handle the pair text.
        if ( text != null && text.length() > 0
                && ! ( config.CLEAR_STOPWORD && dic.match(ILexicon.STOP_WORD, text) ) ) {
            w2 = new Word( text, ILexicon.CJK_WORD );
            w2.setPartSpeechForNull(IWord.PPT_POSPEECH);
            w2.setPosition(pos+1);
            
            if ( w == null ) w = w2;
            else wordPool.add(w2);
        }
        
        /* here: 
         * 1. the punctuation is clear.
         * 2. the pair text is null or being cleared.
         * @date 2013-09-06
        */
        if ( w == null && w2 == null ) {
            return null;
        }
        
        return w;
    }
    
    /**
     * check and append the pinyin and the synonyms words of the specified word
     * 
     * @param   word
    */
    protected void appendCJKWordFeatures( IWord word )
    {
        //add the pinyin to the pool
        if ( config.APPEND_CJK_PINYIN 
                && config.LOAD_CJK_PINYIN && word.getPinyin() != null ) {
        	SegKit.appendPinyin(config, wordPool, word);
        }
        
        //add the synonyms words to the pool
        if ( config.APPEND_CJK_SYN 
                && config.LOAD_CJK_SYN && word.getSyn() != null ) {
            SegKit.appendSynonyms(config, wordPool, word);
        }
    }
    
    /**
     * Check and append the synonyms/pinyin words of specified word included the CJK and basic Latin words
     * All the synonyms words share the same position part of speech, word type with the primitive word
     * 
     * @param w
     */
    protected void appendLatinWordFeatures( IWord w )
    {
        IWord ew, t;
        boolean append_syn = (config.LOAD_CJK_SYN && config.APPEND_CJK_SYN);
        boolean append_pinyin = (config.LOAD_CJK_PINYIN && config.APPEND_CJK_PINYIN);
        if ( (append_syn && w.getSyn() == null) 
        		&& (append_pinyin && w.getPinyin() == null) ) {
        	/*
             * @added 2014-07-07
             * w maybe EC_MIX_WORD, so check its syn first
             * and make sure it is not a EC_MIX_WORD then check the EN_WORD 
            */
        	ew = (t = dic.get(ILexicon.CJK_WORD, w.getValue())) == null ? w : t;
        } else {
        	ew = w;
        }
        
        ew.setPosition(w.getPosition());
        if ( append_syn && ew.getSyn() != null ) {
    		SegKit.appendSynonyms(config, wordPool, ew);
        }
        if ( append_pinyin && ew.getPinyin() != null ) {
    		SegKit.appendPinyin(config, wordPool, ew);
    	}
    }
    
    /**
     * interface to check and do the English secondary segmentation. 
     * Override this method to control the secondary logic.
     * 
     * @param	w
     * @return	boolean
    */
    protected boolean enSecondSegFilter(IWord w)
    {
    	return w.getType() != IWord.T_MIXED_WORD;
    }
    
    /**
     * <p>
     * Do the secondary split for the specified complex Latin word
     * This will split a complex English, Arabic, punctuation compose word to multiple simple parts
     * Like 'qq2013' will split to 'qq' and '2013'
     * </p>
     * 
     * <p>
     * And all the sub words share the same type and part of speech with the primitive word
     * You should check the config.EN_SECOND_SEG before invoke this method
     * </p>
     * 
     * @param  w
     * @param  wList
     * @return LinkedList<IWord> all the sub word tokens
     */
    protected LinkedList<IWord> enSecondSeg( IWord w, LinkedList<IWord> wList ) 
    {
        char[] chars = w.getValue().toCharArray();
        int _TYPE, _ctype, j, pos = w.getPosition();
        IWord tw = null;
        String _str = null;
        
        /* check and create the sub word token buffer */
        if ( wList == null ) {
        	wList = new LinkedList<>();
        }
        
        /* check and keep the original Latin word */
        if ( config.isKeepEnSecOriginalWord() ) {
        	wList.add(w);
        }
        
        for (  j = 0; j < chars.length; ) {
        	/* get the char type.
             * It could only be one of EN_LETTER, EN_NUMERIC, EN_PUNCTUATION. */
        	_TYPE = StringUtil.getEnCharType(chars[j]);
        	if ( _TYPE == StringUtil.EN_PUNCTUATION ) {
        		tw = new Word(String.valueOf(chars[j]), IWord.T_PUNCTUATION);
            	tw.setPartSpeechForNull(IWord.PUNCTUATION);
            	tw.setPosition(pos + j);
            	j++;
        		continue;
        	}
        	
            isb.clear().append(chars[j]);
            for ( int i = j + 1; i < chars.length; i++ ) {
            	_ctype = StringUtil.getEnCharType(chars[i]);
            	if ( _ctype != _TYPE ) {
            		break;
            	}
            	isb.append(chars[i]);
            }
            
            /* If the number of chars is larger than
             *  config.EN_SEC_MIN_LEN we create a new IWord
             * and add to the wordPool.
            */
            if ( isb.length() >= config.EN_SEC_MIN_LEN ) {
                _str = isb.toString();
                if ( ! ( config.CLEAR_STOPWORD && dic.match(ILexicon.STOP_WORD, _str) ) ) {
                	tw = new Word(_str, w.getType());
                	tw.setPartSpeechForNull(w.getPartSpeech());
                	tw.setPosition(pos + j);
                    /* check and do the English word segmentation  */
                    if ( config.EN_WORD_SEG && _TYPE == StringUtil.EN_LETTER ) {
                    	enWordSeg(tw, wList);
                    } else {
                    	wList.addLast(tw);
                    }
                }
            }
            
            j += isb.length();
        }
        
        chars = null;    //Let gc do its work.
        return wList;
    }
    
    /**
     * Latin word lexicon based English word segmentation.
     * 
     * @param	w
     * @param	wList
     * @return	LinkedList<IWord> all the sub word tokens
    */
    protected LinkedList<IWord> enWordSeg(IWord w, LinkedList<IWord> wList) 
    {
    	final char[] chars = w.getValue().toCharArray();
    	IChunk chunk = null;
    	IWord word = null;
    	
    	int index = 0, pos = w.getPosition();
    	while ( index < chars.length ) {
    		chunk = getBestChunk(chars, index, config.EN_MAX_LEN);
    		// word = chunk.getWords()[0];
    		word = chunk.getWords()[0].clone();
			word.setPosition(pos+index);
    		wList.add(word);
    		index += word.getValue().length();
    	}
    	
    	return wList;
    }
    
    /**
     * match the next CJK word in the dictionary
     * 
     * @param  maxLen
     * @param  chars
     * @param  index
     * @param  wList
     * @return IWord[]
     */
    protected IWord[] getNextMatch(int maxLen, char[] chars, int index, List<IWord> wList) 
    {
        final IStringBuffer sb = new IStringBuffer(maxLen);
        sb.clear().append(chars[index]);
        
        /* check and create the default word list */
        // ArrayList<IWord> mList = new ArrayList<IWord>(8);
        if ( wList == null ) {
        	wList = new ArrayList<>(8);
        }
        
        String temp = sb.toString();
        if ( dic.match(ILexicon.CJK_WORD, temp) ) {
        	wList.add(dic.get(ILexicon.CJK_WORD, temp));
        }
        
        String _key = null;
        for ( int j = 1; 
            j < maxLen && ((j+index) < chars.length); j++ ) {
        	sb.append(chars[j+index]);
            _key = sb.toString();
            if ( dic.match(ILexicon.CJK_WORD, _key) ) {
            	wList.add(dic.get(ILexicon.CJK_WORD, _key));
            }
        }
        
        /*
         * if match no words from the current position 
         * to idx+Config.MAX_LENGTH, just return the Word with
         * a value of temp as a unidentified word. 
        */
        if ( wList.isEmpty() ) {
        	wList.add(new Word(temp, ILexicon.UNMATCH_CJK_WORD));
        }
        
        /// for ( int j = 0; j < mList.size(); j++ ) {
        ///     System.out.println(mList.get(j));
        /// }
        
        final IWord[] words = new IWord[wList.size()];
        wList.toArray(words);
        wList.clear();
        
        return words;
    }
    
    /**
     * find an Chinese name from the current position of the input chars
     * 
     * @param chars
     * @param index
     * @param chunk
     * @return IWord
     */
    protected String findCHName( char[] chars, int index, IChunk chunk ) 
    {
        final StringBuilder isb = new StringBuilder();
        /// isb.clear();
        // there is only two IWords in the chunk.
        if ( chunk.getWords().length == 2 ) {
            IWord w = chunk.getWords()[1];
            switch ( w.getLength() ) {
            case 1:
                if ( dic.match(ILexicon.CN_SNAME, w.getValue()) ) {
                    isb.append(w.getValue());
                    return isb.toString();
                }
                return null;
            case 2:
            case 3:
                /*
                 * there is only two IWords in the chunk.
                 * case 2:
                 * like: 这本书是陈高的, chunk: 陈_高的
                 * more: 瓜子和坚果,chunk: 和_坚果 (1.6.8前版本有歧义)
                 * case 3:
                 * 1.double name: the two chars and char after it make up a word.
                 * like: 这本书是陈美丽的, chunk: 陈_美丽的
                 * 2.single name: the char and the two chars after it make up a word. -ignore
                 */
                String d1 = w.getValue().charAt(0) + "";
                String d2 = w.getValue().charAt(1) + "";
                if ( dic.match(ILexicon.CN_DNAME_1, d1)
                        && dic.match(ILexicon.CN_DNAME_2, d2)) {
                    isb.append(d1);
                    isb.append(d2);
                    return isb.toString();
                } 
                /*
                 * the name char of the single name and the char after it
                 *         make up a word. 
                 */
                else if ( dic.match(ILexicon.CN_SNAME, d1) ) {
                    IWord iw = dic.get(ILexicon.CJK_WORD, d2);
                    if ( iw != null && iw.getFrequency() >= config.NAME_SINGLE_THRESHOLD ) {
                        isb.append(d1);
                        return isb.toString();
                    }
                }
                
                return null;
            }
        } else {
            /*three IWords in the chunk */
            IWord w1 = chunk.getWords()[1];
            IWord w2 = chunk.getWords()[2];
            switch ( w1.getLength() ) {
            case 1:
                /*check if it is a double name first.*/
                if ( dic.match(ILexicon.CN_DNAME_1, w1.getValue()) ) {
                    if ( w2.getLength() == 1 ) {
                        /*real double name?*/
                        if ( dic.match(ILexicon.CN_DNAME_2, w2.getValue()) ) {
                            isb.append(w1.getValue());
                            isb.append(w2.getValue());
                            return isb.toString();
                        }
                        /*not a real double name, check if it is a single name.*/
                        else if ( dic.match(ILexicon.CN_SNAME, w1.getValue()) ) {
                            isb.append(w1.getValue());
                            return isb.toString();
                        }
                    } 
                    /*
                     * double name:
                     * char 2 and the char after it make up a word.
                     * like: 陈志高兴奋极了, chunk:陈_志_高兴 (兴和后面成词)
                     * like: 陈志高的, chunk:陈_志_高的 ("的"的阕值Config.SINGLE_THRESHOLD)
                     * like: 陈高兴奋极了, chunk:陈_高_兴奋 (single name)
                     */
                    else {
                        String d1 = w2.getValue().charAt(0) + "";
                        int index_ = index + chunk.getWords()[0].getLength() + 2;
                        IWord[] ws = getNextMatch(config.MAX_LENGTH, chars, index_, null);
                        //System.out.println("index:"+index+":"+chars[index]+", "+ws[0]);
                        /*is it a double name?*/
                        if ( dic.match(ILexicon.CN_DNAME_2, d1) && 
                                (ws.length > 1 || ws[0].getFrequency() >= config.NAME_SINGLE_THRESHOLD)) {
                            isb.append(w1.getValue());
                            isb.append(d1);
                            return isb.toString();
                        }
                        /*check if it is a single name*/
                        else if ( dic.match(ILexicon.CN_SNAME, w1.getValue()) ) {
                            isb.append(w1.getValue());
                            return isb.toString();
                        }
                    }
                } 
                /*check if it is a single name.*/
                else if ( dic.match(ILexicon.CN_SNAME, w1.getValue()) ) {
                    isb.append(w1.getValue());
                    return isb.toString();
                }
                
                return null;
            case 2:
                String d1 = w1.getValue().charAt(0) + "";
                String d2 = w1.getValue().charAt(1) + "";
                /*
                 * it is a double name and char 1, char 2 make up a word.
                 * like: 陈美丽是对的, chunk: 陈_美丽_是
                 * more: 都成为高速公路, chunk:都_成为_高速公路 (1.6.8以前的有歧义)
                 */
                if ( dic.match(ILexicon.CN_DNAME_1, d1)
                        && dic.match(ILexicon.CN_DNAME_2, d2)) {
                    isb.append(w1.getValue());
                    return isb.toString();
                }
                /*
                 * it is a single name, char 1 and the char after it make up a word.
                 */
                else if ( dic.match(ILexicon.CN_SNAME, d1) ) {
                    IWord iw = dic.get(ILexicon.CJK_WORD, d2);
                    if ( iw != null && iw.getFrequency() >= config.NAME_SINGLE_THRESHOLD ) {
                        isb.append(d1);
                        return isb.toString();
                    }
                }
                
                return null;
            case 3:
                /*
                 * single name:  - ignore
                 *  mean the char and the two chars after it make up a word.
                 *  
                 * it is a double name.
                 * like: 陈美丽的人生， chunk: 陈_美丽的_人生
                 */
                String c1 = w1.getValue().charAt(0) + "";
                String c2 = w1.getValue().charAt(1) + "";
                IWord w3 = dic.get(ILexicon.CJK_WORD, w1.getValue().charAt(2)+"");
                if ( dic.match(ILexicon.CN_DNAME_1, c1)
                        && dic.match(ILexicon.CN_DNAME_2, c2)
                        && (w3 == null || w3.getFrequency() >= config.NAME_SINGLE_THRESHOLD)) {
                    isb.append(c1);
                    isb.append(c2);
                    return isb.toString();
                }
                
                return null;
            }
        }
        
        return null;
    }
    
    /**
     * load a CJK char list from the stream start from the 
     * current position till the char is not a CJK char
     * 
     * @param  c
     * @return char[]
     */
    protected char[] nextCJKSentence( int c ) throws IOException 
    {
        isb.clear();
        int ch;
        isb.append((char)c);
        
        //reset the CE check mask.
        ctrlMask &= ~ISegment.CHECK_CE_MASk;
        
        while ( (ch = readNext()) != -1 ) {
            if ( StringUtil.isWhitespace(ch) ) {
                pushBack(ch);
                break;
            }
            
            if ( ! StringUtil.isCJKChar(ch) ) {
                pushBack(ch);
                /*check Chinese English mixed word*/
                if ( StringUtil.isEnLetter(ch) 
                        || StringUtil.isEnNumeric(ch) ) {
                    ctrlMask |= ISegment.CHECK_CE_MASk;
                }
                break;
            } 
            
            isb.append((char)ch);
        }
        
        return isb.toString().toCharArray();
    }
    
    /**
     * find the letter or digit word from the current position 
     * count until the char is whitespace or not letter_digit
     * 
     * @param  c
     * @param  pos
     * @return IWord
     */
    protected IWord nextLatinWord(int c, int pos) throws IOException 
    {
        isb.clear();
        if ( c > 65280 ) c -= 65248;
        if ( c >= 65 && c <= 90 ) c += 32; 
        isb.append((char)c);
        
        int ch;
        //EC word, single units control variables.
        boolean _check  = false;
        boolean _wspace = false;
        
        //Secondary segmentation
        int _ctype = 0;
        int tcount = 1;                                  //number of different char type.
        int _TYPE  = StringUtil.getEnCharType(c);        //current char type.
        ctrlMask &= ~ISegment.START_SS_MASK;            //reset the secondary segment mask.
        
        while ( ( ch = readNext() ) != -1 ) {
            //Covert the full-width char to half-width char.
            if ( ch > 65280 ) ch -= 65248;
            _ctype = StringUtil.getEnCharType(ch);
            
            //Whitespace check.
            if ( _ctype == StringUtil.EN_WHITESPACE ) {
                _wspace = true;
                break;
            }
            
            //English punctuation check.
            if ( _ctype == StringUtil.EN_PUNCTUATION ) {
                if ( ! config.isKeepPunctuation((char)ch) ) {
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
            
            //append the char to the buffer.
            isb.append((char)ch);
            
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
            
            /*
             * global English word length limitation 
            */
            if ( isb.length() > config.MAX_LATIN_LENGTH ) {
                break;
            }
        }
        
        String __str = isb.toString();
        IWord w = null;
        boolean chkunits = true;
        
        /* 
         * @step 2: 
         * 1. clear the useless English punctuation from the end.
         * 2. try to find the English and punctuation mixed word.
         * 
         * set _ctype as the status for the existence of punctuation
         * at the end of the isb because we need to plus the tcount
         * to avoid the secondary check for words like chenxin+, c+.
        */
        _ctype = 0;
        for ( int t = isb.length() - 1; t > 0 
                && isb.charAt(t) != '%'
                && StringUtil.isEnPunctuation(isb.charAt(t)); t-- ) {
            /*
             * try to find a English and punctuation mixed word.
             * this will clear all the punctuation until a mixed word is found.
             * like "i love c++.", c++ will be found from token "c++.".
             * 
             * @Note: added at 2013/08/31 
            */
            if ( dic.match(ILexicon.CJK_WORD, __str) ) {
                w = dic.get(ILexicon.CJK_WORD, __str).clone();
                w.setType(IWord.T_MIXED_WORD);
                w.setPartSpeechForNull(IWord.EN_POSPEECH);
                chkunits = false;
                break;
            }
            
            /*
             * keep the English punctuation.
             * @date 2013/09/06 
             */
            pushBack(isb.charAt(t));
            isb.deleteCharAt(t);
            __str = isb.toString();
            
            /*check and plus the tcount.*/
            if ( _ctype == 0 ) {
                tcount--;
                _ctype = 1;
            }
        }
        
        /* condition to start the secondary segmentation.
         * Since 2.5.1 we also do the secondary tokenize 
         * for English and punctuation mixed word */
        boolean ssseg = (tcount > 1);
        
        /* @step 3: check the end condition.
         * and the check if the token loop was break by whitespace
         * cause there is no need to continue all the following work if it is.
         * @added 2013/11/19 
        */
        if ( ch == -1 || _wspace ) {
        	if ( w == null ) {
        		w = wordNewOrClone(ILexicon.CJK_WORD, __str, IWord.T_BASIC_LATIN);
                w.setPartSpeechForNull(IWord.EN_POSPEECH);
        	}
            if ( ssseg ) ctrlMask |= ISegment.START_SS_MASK;
            return w;
        }
        
        if ( ! _check ) {
            /* 
             * @reader: (2013-09-25)
             * we check the units here, so we can recognize
             * many other units that is not Chinese like '℉,℃' eg..
            */
            if ( chkunits && ( StringUtil.isDigit(__str) 
                        || StringUtil.isDecimal(__str) ) ) {
                ch = readNext();
                if ( dic.match(ILexicon.CJK_UNIT, ((char)ch)+"") ) {
                    w = wordNewOrClone(ILexicon.CJK_WORD, new String(__str+((char)ch)), IWord.T_MIXED_WORD);
                    w.setPartSpeechForNull(IWord.NUMERIC_POSPEECH);
                } else {
                    pushBack(ch);
                }
            }
            
            if ( w == null ) {
                w = wordNewOrClone(ILexicon.CJK_WORD, __str, IWord.T_BASIC_LATIN);
                w.setPartSpeechForNull(IWord.EN_POSPEECH);
                if ( ssseg ) ctrlMask |= ISegment.START_SS_MASK;
            }
            
            return w;
        }
        
        
        //@step 4: check and get English and Chinese mixed word like 'B超'.
        final IStringBuffer iBuffer = new IStringBuffer();
        iBuffer.append(__str);
        String _temp = null;
        int mc = 0, j = 0;        //the number of char that have read from the stream.
        
        //replace width IntArrayList at 2013-09-08
        //ArrayList<Integer> chArr = new ArrayList<Integer>(config.MIX_CN_LENGTH);
        iaList.clear();
        
        /* 
         * Attention:
         * make sure that (ch = readNext()) is after j < Config.MIX_CN_LENGTH.
         * or it will cause the miss of the next char.
         * 
         * @reader: (2013-09-25)
         * we do not check the type of the char that have been read next.
         * so, words started with English and its length except the start English part
         * less than config.MIX_CN_LENGTH in the EC dictionary could be recognized.
         * 
         * @Note added at 2017/08/05
         * Add the iBuffer.length checking logic to follow the limitation
         * of the maximum length of the current token 
        */
        for ( ; j < dic.mixSuffixLength 
                && iBuffer.length() < config.MAX_LENGTH
                    && (ch = readNext()) != -1; j++ ) {
            /* 
             * Attention:
             *  it is an accident that Jcseg works fine for
             *  we break the loop directly when we meet a whitespace.
             *  1. if an EC word is found, unit check process will be ignored.
             *  2. if matches no EC word, certainly return of readNext() 
             *      will make sure the units check process works find.
             */
            if ( StringUtil.isWhitespace(ch) ) {
                pushBack(ch);
                break;
            }
            
            iBuffer.append((char)ch);
            iaList.add(ch);
            _temp = iBuffer.toString();
            if ( dic.match(ILexicon.CJK_WORD, _temp) ) {
                w = dic.get(ILexicon.CJK_WORD, _temp);
                w.setType(IWord.T_MIXED_WORD);
                ctrlMask |= ISegment.START_SS_MASK;
                mc = j + 1;
            }
        }
        
        iBuffer.clear();

        //push back the read chars.
        for ( int i = j - 1; i >= mc; i-- ) pushBack(iaList.get(i));
        //chArr.clear();chArr = null;
        
        /* @step 5: check if there is a units for the digit.
         * @reader: (2013-09-25)
         * now we check the units before the step 4, so we can recognize
         * many other units that is not Chinese like '℉,℃'
        */
        if ( chkunits && mc == 0 ) {
            if ( StringUtil.isDigit(__str) || StringUtil.isDecimal(__str) ) {
                ch = readNext();
                if ( dic.match(ILexicon.CJK_UNIT, ((char)ch)+"") ) {
                    w = wordNewOrClone(ILexicon.CJK_WORD, new String(__str+((char)ch)), IWord.T_MIXED_WORD);
                    w.setPartSpeechForNull(IWord.NUMERIC_POSPEECH);
                } else {
                    pushBack(ch);
                }
            }
        }
        
        /* simply return the combination of English char, Arabic
         * numeric, English punctuation if matches no single units or EC word.
        */
        if ( w == null ) {
            w = wordNewOrClone(ILexicon.CJK_WORD, __str, IWord.T_BASIC_LATIN);
            w.setPartSpeechForNull(IWord.EN_POSPEECH);
            if ( ssseg ) ctrlMask |= ISegment.START_SS_MASK;
        } else if ( mc > 0 ) {
            w = w.clone();
        }
        
        return w;
    }
    
    /**
     * the simple version of the next basic Latin fetch logic
     * Just return the next Latin string with the keep punctuation after it
     * 
     * @param   c
     * @return  String
    */
    protected String nextLatinString(int c) throws IOException
    {
        isb.clear();
        if ( c > 65280 ) c -= 65248;
        if ( c >= 65 && c <= 90 ) c += 32; 
        isb.append((char)c);
        
        int ch;
        int _ctype = 0;
        ctrlMask &= ~ISegment.CHECK_EC_MASK;
        
        while ( (ch = readNext()) != -1 ) {
            //Covert the full-width char to half-width char.
            if ( ch > 65280 ) ch -= 65248;
            _ctype = StringUtil.getEnCharType(ch);
            
            //Whitespace check.
            if ( _ctype == StringUtil.EN_WHITESPACE ) {
                break;
            }
            
            //English punctuation check.
            if ( _ctype == StringUtil.EN_PUNCTUATION ) {
                if ( ! config.isKeepPunctuation((char)ch) ) {
                    pushBack(ch);
                    break;
                }
            }
            
            //Not EN_KNOW, and it could be letter, numeric.
            if ( _ctype == StringUtil.EN_UNKNOW ) {
                pushBack(ch);
                if ( StringUtil.isCJKChar( ch ) ) {
                    ctrlMask |= ISegment.CHECK_EC_MASK;
                }
                
                break;
            }
            
            //covert the lower case letter to upper case.
            if ( ch >= 65 && ch <= 90 ) ch += 32;
            isb.append((char)ch);
            
            /*
             * global English word length limitation 
            */
            if ( isb.length() > config.MAX_LATIN_LENGTH ) {
                break;
            }
        }
        
        /*
         * check and remove the dot punctuation after it 
        */
        for ( int j = isb.length() - 1; j > 0; j-- ) {
            if ( isb.charAt(j) == '.' ) {
                isb.deleteCharAt(j);
            }
        }
        
        return isb.toString();
    }
    
    /**
     * find the next other letter from the current position
     * find the letter number from the current position
     * count until the char in the specified position is not a letter number or whitespace
     * 
     * @param c
     * @return String
     */
    protected String nextLetterNumber( int c ) throws IOException 
    {
        //StringBuilder isb = new StringBuilder();
        isb.clear();
        isb.append((char)c);
        int ch;
        while ( (ch = readNext()) != -1 ) {
            if ( StringUtil.isWhitespace(ch) ) {
                pushBack(ch);
                break;
            }
            
            if ( ! StringUtil.isLetterNumber( ch ) ) {
                pushBack(ch);
                break;
            }
            
            isb.append((char)ch);
        }
        
        return isb.toString();
    }
    
    /**
     * find the other number from the current position
     * count until the char in the specified position is not a other number or whitespace
     * 
     * @param   c
     * @return  String
     */
    protected String nextOtherNumber( int c ) throws IOException 
    {
        //StringBuilder isb = new StringBuilder();
        isb.clear();
        isb.append((char)c);
        int ch;
        while ( (ch = readNext()) != -1 ) {
            if ( StringUtil.isWhitespace(ch) ) {
                pushBack(ch);
                break;
            }
            
            if ( ! StringUtil.isOtherNumber(ch) ) {
                pushBack(ch);
                break;
            } 
            
            isb.append((char)ch);
        }
        
        return isb.toString();
    }
    
    /**
     * find the Chinese number from the current position
     * count until the char in the specified position is not a other number or whitespace
     * 
     * @param chars char array of CJK items
     * @param index
     * @return String[]
     */
    protected String nextCNNumeric( char[] chars, int index ) throws IOException 
    {
        //StringBuilder isb = new StringBuilder();
        isb.clear();
        isb.append( chars[ index ]);
        ctrlMask &= ~ISegment.CHECK_CF_MASK;        //reset the fraction check mask.
        
        for ( int j = index + 1; j < chars.length; j++ ) {
            /* 
             * check and deal with '分之' if the 
             * current char is not a Chinese numeric. 
             * (try to recognize a Chinese fraction)
             * 
             * @added 2013-12-14
            */
            if ( NumericUtil.isCNNumeric(chars[j]) == -1 ) {
                if ( j + 2 < chars.length 
                        && chars[j  ] == '分' 
                        && chars[j+1] == '之'
                        /* check and make sure chars[j+2] is a chinese numeric.
                         * or error will happen on situation like '四六分之' .
                         * @added 2013-12-14 */
                        && NumericUtil.isCNNumeric(chars[j+2]) != -1 ) {
                    isb.append(chars[j++]);
                    isb.append(chars[j++]);
                    isb.append(chars[j  ]);
                    //set the chinese fraction check mask.
                    ctrlMask |= ISegment.CHECK_CF_MASK;
                    continue;
                } else {
                    break;
                }
            }
            
            //append the buffer.
            isb.append( chars[j] );
        }
        
        return isb.toString();
    }
    
    /**
     * find pair punctuation of the given punctuation char
     * the purpose is to get the text between them
     * 
     * @param c
     */
    protected String getPairPunctuationText( int c ) throws IOException 
    {
        //StringBuilder isb = new StringBuilder();
        isb.clear();
        char echar = StringUtil.getPunctuationPair( (char) c);
        boolean matched = false;
        int j, ch;
        
        //replaced with IntArrayList at 2013-09-08
        //ArrayList<Integer> chArr = new ArrayList<Integer>(config.PPT_MAX_LENGTH);
        iaList.clear();
        
        for ( j = 0; j < config.PPT_MAX_LENGTH; j++ ) {
            ch = readNext();
            if ( ch == -1 ) break;
            if ( ch == echar ) {
                matched = true;
                pushBack(ch);        //push the pair punc back.
                break;
            }
            
            isb.append( (char) ch );
            iaList.add(ch);
        }
        
        if (!matched) {
            for ( int i = j - 1; i >= 0; i-- ) {
                pushBack(iaList.get(i));
            }
            return null;
        }
        
        return isb.toString();
    }
    
    
    /**
     * check if the specified word is existed in a specified dictionary
     * and if it does clone it or create a new one.
     * Note: why we need this ?
     * clone will extend all the features from the original word item
     * including part of speech, pinyin, synonyms etc.
     * 
     * @param	t
     * @param	str
     * @param	type
    */
    public IWord wordNewOrClone(int t, String str, int type)
    {
    	return dic.match(t, str) ? dic.get(t, str).clone() : new Word(str, type);
    }
    
    /**
     * an abstract method to get word from the 
     * current position with MMSEG algorithm. 
     * simpleSeg and ComplexSeg is different to deal with this so make it a abstract method here
     * 
     * @param  chars
     * @param  index
     * @param  maxLen
     * @return IChunk
     */
    protected IChunk getBestChunk(char[] chars, int index, int maxLen)
    {
    	return null;
    }
    
}