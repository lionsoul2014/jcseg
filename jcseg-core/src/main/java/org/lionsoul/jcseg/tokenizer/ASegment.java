package org.lionsoul.jcseg.tokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;

import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.IChunk;
import org.lionsoul.jcseg.tokenizer.core.ILexicon;
import org.lionsoul.jcseg.tokenizer.core.ISegment;
import org.lionsoul.jcseg.tokenizer.core.IWord;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;
import org.lionsoul.jcseg.util.NumericUtil;
import org.lionsoul.jcseg.util.StringUtil;
import org.lionsoul.jcseg.util.IPushbackReader;
import org.lionsoul.jcseg.util.IStringBuffer;
import org.lionsoul.jcseg.util.IntArrayList;

/**
 * abstract segmentation super class:
 * 1. implemented the ISegment interface 
 * 2. implemented all the common functions
 * that simple, complex, most segmentation algorithm will all shared.
 * 
 * @author  chenxin <chenxin619315@gmail.com>
*/
public abstract class ASegment implements ISegment 
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
    protected LinkedList<IWord> wordPool = null;
    //protected IHashQueue<IWord> wordPool = null;
    protected IStringBuffer isb;
    protected IntArrayList ialist;
    
    /**
     * segmentation runtime function control mask
    */
    protected int ctrlMask = 0;
    
    /**
     * the dictionary and task configuration instance
    */
    protected ADictionary dic;
    protected JcsegTaskConfig config;
    
    /**
     * initialize the segment
     * 
     * @param   input
     * @param   config Jcseg task configuration instance
     * @param   dic Jcseg dictionary instance
     * @throws  IOException
    */
    public ASegment( Reader input, JcsegTaskConfig config, ADictionary dic ) throws IOException 
    {
        this.config = config;
        this.dic    = dic;
        wordPool    = new LinkedList<IWord>();
        isb         = new IStringBuffer(64);
        ialist      = new IntArrayList(15);
        reset(input);
    }
    
    /**
     * @see #ASegment(Reader, JcsegTaskConfig, ADictionary) 
    */
    public ASegment( JcsegTaskConfig config, ADictionary dic ) throws IOException 
    {
        this(null, config, dic);
    }
    
    /**
     * input stream and reader reset.
     * 
     * @param input
     * @throws IOException
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
     * 
     * @throws IOException 
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
     * @param data
     * @throws IOException 
     */
    protected void pushBack( int data ) throws IOException 
    {
        reader.unread(data);
        idx--;
    }
    
    @Override
    public int getStreamPosition() 
    {
        return idx + 1;
    }
    
    /**
     * set the dictionary of the current tokenizer.
     * 
     * @param    dic
     */
    public void setDict( ADictionary dic ) 
    {
        this.dic = dic;
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
     * set the current task configuration instance.
     * 
     * @param    config
     */
    public void setConfig( JcsegTaskConfig config ) 
    {
        this.config = config;
    }
    
    /**
     * get the current task configuration instance.
     * 
     * @return    JcsegTaskConfig
     */
    public JcsegTaskConfig getConfig() 
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
                word = getNextCJKWord(c, pos);
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
                    && StringUtil.isPairPunctuation( (char) c ) ) {
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
                word.setPartSpeech(IWord.NUMERIC_POSPEECH);
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
                word.setPartSpeech(IWord.NUMERIC_POSPEECH);
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
                word.setPartSpeech(IWord.PUNCTUATION);
                word.setPosition(pos);
            }
            /* @reader: (2013-09-25) 
             * unrecognized char will cause unknow problem for different system.
             * keep it or clear it ?
             * if you use jcseg for search, better shut it down.
            */
            else if ( config.KEEP_UNREG_WORDS ) {
                String str = String.valueOf((char)c);
                if ( config.CLEAR_STOPWORD
                        && dic.match(ILexicon.STOP_WORD, str)) {
                    continue;
                }
                
                word = new Word(str, IWord.T_UNRECOGNIZE_WORD);
                word.setPartSpeech(IWord.UNRECOGNIZE);
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
             * or if the continue lead the next loop to the end of the stream
             * then the buffered word will miss
            */
            if ( wordPool.size() > 0 ) {
                return wordPool.removeFirst();
            }
            
            continue;
        }
        
        return null;
    }
    
    /**
     * get the next CJK word from the current position of the input stream
     * 
     * @param   c
     * @param   pos
     * @return  IWord could be null and that mean we reached a stop word
     * @throws  IOException 
    */
    protected IWord getNextCJKWord(int c, int pos) throws IOException
    {
        char[] chars = nextCJKSentence(c);
        int cjkidx = 0;
        IWord w = null;
        while ( cjkidx < chars.length ) {
            /*
             * find the next CJK word.
             * the process will be different with the different algorithm
             * @see getBestCJKChunk() from SimpleSeg or ComplexSeg. 
             */
            w = null;
            
            
            //-----------------------------------------------------------
            
            /*
             * @istep 1: 
             * 
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
                 * checkCF will be reset to be 'TRUE' it num is a chinese fraction.
                 * @added 2013-12-14.
                 * */
                if ( (ctrlMask & ISegment.CHECK_CF_MASK) != 0  ) {
                    //get the Chinese fraction.
                    w = new Word(num, IWord.T_CN_NUMERIC);
                    w.setPosition(pos+cjkidx);
                    w.setPartSpeech(IWord.NUMERIC_POSPEECH);
                    wordPool.add(w);
                    
                    /* Here: 
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
                        wd.setPartSpeech(IWord.NUMERIC_POSPEECH);
                        wordPool.add(wd);
                    }
                }
                /*
                 * check the Chinese numeric and single units.
                 * type to find Chinese and unit composed word.
                 * */
                else if ( NumericUtil.isCNNumeric(chars[cjkidx+1]) > -1
                        || dic.match(ILexicon.CJK_UNITS, chars[cjkidx+1]+"") ) {
                    StringBuilder sb = new StringBuilder();
                    String temp      = null;
                    String ONUM      = num;    //backup the old numeric
                    sb.append(num);
                    boolean matched = false;
                    int j;
                    
                    //find the word that made up with the numeric
                    //like: 五四运动
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
                    if ( matched == true && num.length() - NUMLEN == 1 
                            && dic.match(ILexicon.CJK_UNITS, num.substring(NUMLEN)) ) {
                        num     = ONUM;
                        matched = false;    //reset the matched
                    }
                    
                    //find the numeric units
                    IWord wd = null;
                    if ( matched == false && config.CNNUM_TO_ARABIC ) {
                        //get the numeric'a Arabic
                        String arabic = NumericUtil.cnNumericToArabic(num, true)+"";
                        if ( (cjkidx + num.length()) < chars.length
                                && dic.match(ILexicon.CJK_UNITS, chars[cjkidx + num.length()]+"") ) {
                            char units = chars[ cjkidx + num.length() ];
                            num += units; arabic += units;
                        }
                        
                        wd = new Word( arabic, IWord.T_CN_NUMERIC);
                        wd.setPartSpeech(IWord.NUMERIC_POSPEECH);
                        wd.setPosition(pos+cjkidx);
                    }
                    
                    //clear the stop words as need
                    if ( config.CLEAR_STOPWORD 
                            && dic.match(ILexicon.STOP_WORD, num) ) {
                        cjkidx += num.length();
                        continue;
                    }
                    
                    /*
                     * @Note: added at 2016/07/19
                     * we cannot share the position with the original word item in the
                     * global dictionary accessable with this.dic
                     * 
                     * cuz at the concurrency that will lead to the position error
                     * so, we clone it if the word is directly get from the dictionary
                    */
                    if ( w == null ) {
                        w = new Word( num, IWord.T_CN_NUMERIC );
                        w.setPartSpeech(IWord.NUMERIC_POSPEECH);
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
                    appendWordFeatures(w);
                    continue;
                }
            }
            
            
            //-------------------------------------------------------------
            IChunk chunk = getBestCJKChunk(chars, cjkidx);
            //System.out.println(chunk+"\n");
            //w = new Word(chunk.getWords()[0].getValue(), IWord.T_CJK_WORD);
            w = chunk.getWords()[0];
            
            /* 
             * @istep 2: 
             * 
             * check and try to find a Chinese name.
             */
            int T = -1;
            if ( config.I_CN_NAME
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
                /*else if ( w.getLength() > 1
                        && findCHName( w, chunk ))  {
                    T = IWord.T_CN_NAME;
                    sb.append(chunk.getWords()[1].getValue().charAt(0));
                }*/
                
                if ( T != -1 ) {
                    w = new Word(sb.toString(), T);
                    //w.setPosition(pos+cjkidx);
                    w.setPartSpeech(IWord.NAME_POSPEECH);
                }
            }
            
            //check the stopwords(clear it when Config.CLEAR_STOPWORD is true)
            if ( config.CLEAR_STOPWORD 
                    && dic.match(ILexicon.STOP_WORD, w.getValue()) ) {
                cjkidx += w.getLength();
                continue;
            }
            
            
            //---------------------------------------------------------
            
            /*
             * @istep 3:
             * 
             * reach the end of the chars - the last word.
             * check the existence of the Chinese and English mixed word
             */
            IWord enAfter = null, ce = null;
            if ( ( ctrlMask & ISegment.CHECK_CE_MASk ) != 0 
                    && (cjkidx + w.getLength() >= chars.length) ) {
                //System.out.println("CE-Word"+w.getValue());
                enAfter = nextBasicLatin(readNext());
                //if ( enAfter.getType() == IWord.T_BASIC_LATIN ) {
                String cestr = w.getValue() + enAfter.getValue();
                
                /*
                 * here: (2013-08-31 added)
                 * also make sure the CE word is not a stop word
                 */
                if ( ! ( config.CLEAR_STOPWORD 
                    && dic.match(ILexicon.STOP_WORD, cestr) )
                    && dic.match(ILexicon.CE_MIXED_WORD, cestr) ) {
                    ce = dic.get(ILexicon.CE_MIXED_WORD, cestr).clone();
                    ce.setPosition(pos+cjkidx);
                    wordPool.add(ce);
                    cjkidx += w.getLength();
                    enAfter = null;
                }
                //}
            }
            
            /*
             * no ce word found, store the English word.
             * 
             * @reader: (2013-08-31 added)
             * the newly found letter or digit word "enAfter" token 
             * will be handled at last cause we have to handle 
             * the pinyin and the syn words first.
             * 
             * @Note: added at 2016/07/19
             * if the ce word is null and if the T is -1
             * the w should be a word that clone from itself
             */
            if ( ce == null ) {
                if ( T == -1 ) w = w.clone();
                w.setPosition(pos+cjkidx);
                wordPool.add(w);
                cjkidx += w.getLength();
            } else {
                w = ce;
            }
            
            
            //-------------------------------------------------------

            /*
             * @istep 4:
             * 
             * check and append the pinyin and the synonyms words.
             */
            if ( T == -1 ) {
                appendWordFeatures(w);
            }
            
            //handle the after English word
            //generated at the above Chinese and English mix word
            if ( enAfter != null && ! ( config.CLEAR_STOPWORD 
                    && dic.match(ILexicon.STOP_WORD, enAfter.getValue()) ) ) {
                enAfter.setPosition(pos+chars.length);
                //check and to the secondary split.
                if ( config.EN_SECOND_SEG
                        && (ctrlMask & ISegment.START_SS_MASK) != 0 )  {
                    enSecondSeg(enAfter, false);
                }
                
                wordPool.add(enAfter);
                
                //append the synonyms words.
                if ( config.APPEND_CJK_SYN ) {
                    appendLatinSyn(enAfter);
                }
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
     * @throws  IOException 
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
            w.setPartSpeech(IWord.PUNCTUATION);
            return w;
        }
        
        //get the next basic Latin token.
        IWord w = nextBasicLatin(c);
        w.setPosition(pos);
        
        /* @added: 2013-12-16
         * check and do the secondary segmentation work.
         * This will split 'qq2013' to 'qq, 2013'.
         * */
        if ( config.EN_SECOND_SEG
                && (ctrlMask & ISegment.START_SS_MASK) != 0 ) {
            enSecondSeg(w, false);
        }
        
        //clear the stop word
        if ( config.CLEAR_STOPWORD 
                && dic.match(ILexicon.STOP_WORD, w.getValue()) ) {
            w = null;    //Let gc do its work
            return null;
        }
        
        if ( config.APPEND_CJK_SYN ) {
            appendLatinSyn(w);
        }
        
        return w;
    }
    
    /**
     * get the next punctuation pair word from the current position 
     * of the input stream.
     * 
     * @param   c
     * @param   pos
     * @return  IWord could be null and that mean we reached a stop word
     * @throws  IOException 
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
            w.setPartSpeech(IWord.PUNCTUATION);
            w.setPosition(pos);
        }
        
        //handle the pair text.
        if ( text != null && ! ( config.CLEAR_STOPWORD 
                && dic.match(ILexicon.STOP_WORD, text) ) ) {
            w2 = new Word( text, ILexicon.CJK_WORD );
            w2.setPartSpeech(IWord.PPT_POSPEECH);
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
    protected void appendWordFeatures( IWord word )
    {
        //add the pinyin to the pool
        if ( config.APPEND_CJK_PINYIN 
                && config.LOAD_CJK_PINYIN && word.getPinyin() != null ) {
            IWord pinyin = new Word(word.getPinyin(), IWord.T_CJK_PINYIN);
            pinyin.setPosition(word.getPosition());
            wordPool.add(pinyin);
        }
        
        //add the synonyms words to the pool
        String[] syns = null;
        IWord syn = null;
        if ( config.APPEND_CJK_SYN 
                && config.LOAD_CJK_SYN && (syns = word.getSyn()) != null ) {
            for ( int j = 0; j < syns.length; j++ ) {
                syn = new Word(syns[j], word.getType());
                syn.setPartSpeech(word.getPartSpeech());
                syn.setPosition(word.getPosition());
                wordPool.add(syn);
            }
        }
    }
    
    /**
     * Check and append the synonyms words of specified word included the CJK and basic Latin words
     * All the synonyms words share the same position part of speech, word type with the primitive word
     * 
     * @param w
     */
    protected void appendLatinSyn( IWord w )
    {
        IWord ew;
        
        /*
         * @added 2014-07-07
         * w maybe EC_MIX_WORD, so check its syn first
         * and make sure it is not a EC_MIX_WORD then check the EN_WORD 
         */
        if ( w.getSyn() == null ) {
            ew = dic.get(ILexicon.EN_WORD, w.getValue());
        } else {
            ew = w;
        }
        
        if (  ew != null && ew.getSyn() != null ) {
            IWord sw = null;
            String[] syns = ew.getSyn();
            for ( int j = 0; j < syns.length; j++ ) {
                sw = new Word(syns[j], w.getType());
                sw.setPartSpeech(w.getPartSpeech());
                sw.setPosition(w.getPosition());
                wordPool.add(sw);
            }
        }
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
     * @param  retfw whether to return the fword.
     * @return IWord the first sub token for the secondary segment.
     */
    protected IWord enSecondSeg( IWord w, boolean retfw ) 
    {
        //System.out.println("second: "+w.getValue());
        isb.clear();
        char[] chars = w.getValue().toCharArray();
        int _TYPE = StringUtil.getEnCharType(chars[0]);
        int _ctype, start = 0, j, p = 0;
        
        isb.append(chars[0]);
        IWord sword = null, fword = null;    //first word
        String _str = null;
        
        for ( j = 1; j < chars.length; j++ ) {
            /* get the char type.
             * It could only be one of 
             *     EN_LETTER, EN_NUMERIC, EN_PUNCTUATION.
             * */
            _ctype = StringUtil.getEnCharType(chars[j]);
            if ( _ctype == StringUtil.EN_PUNCTUATION ) {
                _TYPE = StringUtil.EN_PUNCTUATION;
                p++;
                continue;
            }
            
            if ( _ctype == _TYPE ) {
                isb.append(chars[j]);
            } else {
                start = j - isb.length() - p;
                
                /* If the number of chars is larger than
                 *     config.EN_SSEG_LESSLEN we create a new IWord
                 * and add to the wordPool.
                 * */
                if ( isb.length() >= config.STOKEN_MIN_LEN ) {
                    _str = isb.toString();
                    //check and clear the stopwords
                    if ( ! ( config.CLEAR_STOPWORD
                            && dic.match(ILexicon.STOP_WORD, _str) ) ) {
                        sword = new Word(_str, w.getType());
                        sword.setPartSpeech(w.getPartSpeech());
                        sword.setPosition(w.getPosition() + start);
                        if ( retfw && fword == null ) fword = sword;
                        else wordPool.add(sword);
                    }
                }
                
                isb.clear();
                isb.append(chars[j]);
                p = 0;
                _TYPE = _ctype;
            }
        }
        
        //Continue to check the last item.
        if ( isb.length() >= config.STOKEN_MIN_LEN ) {
            start = j - isb.length() - p;
            _str = isb.toString();
            
            if ( ! ( config.CLEAR_STOPWORD
                    && dic.match(ILexicon.STOP_WORD, _str) ) ) {
                sword = new Word(_str, w.getType());
                sword.setPartSpeech(w.getPartSpeech());
                sword.setPosition(w.getPosition() + start);
                if ( retfw && fword == null ) fword = sword;
                else wordPool.add(sword);
            }
        }
        
        chars = null;    //Let gc do its work.

        return fword;
    }
    
    /**
     * match the next CJK word in the dictionary
     * 
     * @param chars
     * @param index
     * @return IWord[]
     */
    protected IWord[] getNextMatch(char[] chars, int index) 
    {
        ArrayList<IWord> mList = new ArrayList<IWord>(8);
        //StringBuilder isb = new StringBuilder();
        isb.clear();
    
        char c = chars[index];
        isb.append(c);
        String temp = isb.toString();
        if ( dic.match(ILexicon.CJK_WORD, temp) ) {
            mList.add(dic.get(ILexicon.CJK_WORD, temp));
        }
        
        String _key = null;
        for ( int j = 1; 
            j < config.MAX_LENGTH && ((j+index) < chars.length); j++ ) {
            isb.append(chars[j+index]);
            _key = isb.toString();
            if ( dic.match(ILexicon.CJK_WORD, _key) ) {
                mList.add(dic.get(ILexicon.CJK_WORD, _key));
            }
        }
        
        /*
         * if match no words from the current position 
         * to idx+Config.MAX_LENGTH, just return the Word with
         * a value of temp as a unrecognited word. 
         */
        if ( mList.isEmpty() ) {
            mList.add(new Word(temp, ILexicon.UNMATCH_CJK_WORD));
        }
        
/*        for ( int j = 0; j < mList.size(); j++ ) {
            System.out.println(mList.get(j));
        }*/
        
        IWord[] words = new IWord[mList.size()];
        mList.toArray(words);
        mList.clear();
        
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
        StringBuilder isb = new StringBuilder();
        //isb.clear();
        /*there is only two IWords in the chunk. */
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
                String d1 = new String(w.getValue().charAt(0)+"");
                String d2 = new String(w.getValue().charAt(1)+"");
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
                        String d1 = new String(w2.getValue().charAt(0)+"");
                        int index_ = index + chunk.getWords()[0].getLength() + 2;
                        IWord[] ws = getNextMatch(chars, index_);
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
                String d1 = new String(w1.getValue().charAt(0)+"");
                String d2 = new String(w1.getValue().charAt(1)+"");
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
                 * singe name:  - ignore
                 *  mean the char and the two chars after it make up a word.
                 *  
                 * it is a double name.
                 * like: 陈美丽的人生， chunk: 陈_美丽的_人生
                 */
                String c1 = new String(w1.getValue().charAt(0)+"");
                String c2 = new String(w1.getValue().charAt(1)+"");
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
     * find the Chinese double name:
     * when the last name and the first char of the name make up a word
     * 
     * @param chunk the best chunk.
     * @return boolean
     */
    @Deprecated
    public boolean findCHName( IWord w, IChunk chunk ) 
    {
        String s1 = new String(w.getValue().charAt(0)+"");
        String s2 = new String(w.getValue().charAt(1)+"");
        
        if ( dic.match(ILexicon.CN_LNAME, s1)
                && dic.match(ILexicon.CN_DNAME_1, s2)) {
            IWord sec = chunk.getWords()[1]; 
            switch ( sec.getLength() ) {
            case 1:
                if ( dic.match(ILexicon.CN_DNAME_2, sec.getValue()) )
                    return true;
            case 2:
                String d1 = new String(sec.getValue().charAt(0)+"");
                IWord _w = dic.get(ILexicon.CJK_WORD, sec.getValue().charAt(1)+"");
                //System.out.println(_w);
                if ( dic.match(ILexicon.CN_DNAME_2, d1)
                        && (_w == null 
                        || _w.getFrequency() >= config.NAME_SINGLE_THRESHOLD ) ) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * load a CJK char list from the stream start from the 
     * current position till the char is not a CJK char
     * 
     * @param c
     * @return char[]
     * @throws IOException
     */
    protected char[] nextCJKSentence( int c ) throws IOException 
    {
        //StringBuilder isb = new StringBuilder();
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
     * @param c
     * @return IWord
     * @throws IOException 
     */
    protected IWord nextBasicLatin( int c ) throws IOException 
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
         * at the end of the isb cause we need to plus the tcount 
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
            if ( dic.match(ILexicon.EN_PUN_WORD, __str) ) {
                w = dic.get(ILexicon.EN_PUN_WORD, __str).clone();
                w.setPartSpeech(IWord.EN_POSPEECH);
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
        
        //condition to start the secondary segmentation.
        boolean ssseg = (tcount > 1) && chkunits;
        
        /* @step 3: check the end condition.
         * and the check if the token loop was break by whitespace
         * cause there is no need to continue all the following work if it is.
         * 
         * @added 2013-11-19 
        */
        if ( ch == -1 || _wspace ) {
            w = new Word(__str, IWord.T_BASIC_LATIN);
            w.setPartSpeech(IWord.EN_POSPEECH);
            if ( ssseg ) ctrlMask |= ISegment.START_SS_MASK;
            return w;
        }
        
        if ( ! _check ) {    
            /* @reader: (2013-09-25)
             * we check the units here, so we can recognize
             * many other units that is not chinese like '℉,℃' eg..
            */
            if ( chkunits && ( StringUtil.isDigit(__str) 
                        || StringUtil.isDecimal(__str) ) ) {
                ch = readNext();
                if ( dic.match(ILexicon.CJK_UNITS, ((char)ch)+"") ) {
                    w = new Word(new String(__str+((char)ch)), IWord.T_MIXED_WORD);
                    w.setPartSpeech(IWord.NUMERIC_POSPEECH);
                } else {
                    pushBack(ch);
                }
            }
            
            if ( w == null ) {
                w = new Word(__str, IWord.T_BASIC_LATIN);
                w.setPartSpeech(IWord.EN_POSPEECH);
                if ( ssseg ) ctrlMask |= ISegment.START_SS_MASK;
            }
            
            return w;
        }
        
        
        //@step 4: check and get english and chinese mix word like 'B超'.
        IStringBuffer ibuffer = new IStringBuffer();
        ibuffer.append(__str);
        String _temp = null;
        int mc = 0, j = 0;        //the number of char that readed from the stream.
        
        //replace width IntArrayList at 2013-09-08
        //ArrayList<Integer> chArr = new ArrayList<Integer>(config.MIX_CN_LENGTH);
        ialist.clear();
        
        /* Attension:
         * make sure that (ch = readNext()) is after j < Config.MIX_CN_LENGTH.
         * or it cause the miss of the next char. 
         * 
         * @reader: (2013-09-25)
         * we do not check the type of the char readed next.
         * so, words started with english and its length except the start english part
         * less than config.MIX_CN_LENGTH in the EC dictionary could be recongnized.
         */
        for ( ; j < config.MIX_CN_LENGTH && (ch = readNext()) != -1; j++ ) {
            /* Attension:
             *  it is a accident that jcseg works find for 
             *  we break the loop directly when we meet a whitespace.
             *  1. if a EC word is found, unit check process will be ignore.
             *  2. if matches no EC word, certianly return of readNext() 
             *      will make sure the units check process works find.
             */
            if ( StringUtil.isWhitespace(ch) ) {
                pushBack(ch);
                break;
            }
            
            ibuffer.append((char)ch);
            //System.out.print((char)ch+",");
            ialist.add(ch);
            _temp = ibuffer.toString();
            //System.out.println((j+1)+": "+_temp);
            if ( dic.match(ILexicon.EC_MIXED_WORD, _temp) ) {
                w  = dic.get(ILexicon.EC_MIXED_WORD, _temp);
                mc = j + 1;
            }
        }
        
        ibuffer.clear();
        ibuffer = null;        //Let gc do it's work.
        
        //push back the readed chars.
        for ( int i = j - 1; i >= mc; i-- ) pushBack(ialist.get(i));
        //chArr.clear();chArr = null;
        
        /* @step 5: check if there is a units for the digit.
         * @reader: (2013-09-25)
         * now we check the units before the step 4, so we can recognize
         * many other units that is not chinese like '℉,℃'
        */
        if ( chkunits && mc == 0 ) {
            if ( StringUtil.isDigit(__str) || StringUtil.isDecimal(__str) ) {
                ch = readNext();
                if ( dic.match(ILexicon.CJK_UNITS, ((char)ch)+"") ) {
                    w = new Word(new String(__str+((char)ch)), IWord.T_MIXED_WORD);
                    w.setPartSpeech(IWord.NUMERIC_POSPEECH);
                } else {
                    pushBack(ch);
                }
            }
        }
        
        /* simply return the combination of english char, arabic
         * numeric, english punctuaton if matches no single units or EC word.
        */
        if ( w == null ) {
            w = new Word(__str, IWord.T_BASIC_LATIN);
            w.setPartSpeech(IWord.EN_POSPEECH);
            if ( ssseg ) ctrlMask |= ISegment.START_SS_MASK;
        } else if ( mc > 0 ) {
            w = w.clone();
        }
        
        return w;
    }
    
    /**
     * find the next other letter from the current position
     * find the letter number from the current position
     * count until the char in the specified position is not a letter number or whitespace
     * 
     * @param c
     * @return String
     * @throws IOException
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
     * count until the char in the specified position is not a orther number or whitespace
     * 
     * @param c
     * @return String
     * @throws IOException
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
     * find the chinese number from the current position
     * count until the char in the specified position is not a orther number or whitespace
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
        
        for ( int j = index + 1;
                    j < chars.length; j++ ) {
            /* check and deal with '分之' if the 
             * current char is not a chinese numeric. 
             * (try to recognize a chinese fraction)
             * @added 2013-12-14
             * */
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
     * the purpose is to get the text bettween them
     * 
     * @param c
     * @throws IOException 
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
        ialist.clear();
        
        for ( j = 0; j < config.PPT_MAX_LENGTH; j++ ) {
            ch = readNext();
            if ( ch == -1 ) break;
            if ( ch == echar ) {
                matched = true;
                pushBack(ch);        //push the pair punc back.
                break;
            }
            
            isb.append( (char) ch );
            ialist.add(ch);
        }
        
        if ( matched == false ) {
            for ( int i = j - 1; i >= 0; i-- ) 
                pushBack( ialist.get(i) );
            return null;
        }
        
        return isb.toString();
    }
    
    /**
     * an abstract method to gain a CJK word from the 
     * current position. simpleSeg and ComplexSeg is different to deal this, 
     * so make it a abstract method here
     * 
     * @param  chars
     * @param  index
     * @return IChunk
     * @throws IOException
     */
    protected abstract IChunk getBestCJKChunk(char chars[], int index) throws IOException;
    
}