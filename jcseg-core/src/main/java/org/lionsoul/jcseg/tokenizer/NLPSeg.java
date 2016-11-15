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
import org.lionsoul.jcseg.util.NumericUtil;

/**
 * NLP segmentation implementation
 * And this extends all the properties of the Complex one
 * the rest of them are build for NLP only
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class NLPSeg extends ComplexSeg
{
    public NLPSeg(Reader input, JcsegTaskConfig config, ADictionary dic) throws IOException
    {
        super(input, config, dic);
        
        /*
         * basic common setting for NLP mode 
        */
        config.EN_SECOND_SEG   = false;
        config.CNFRA_TO_ARABIC = true;
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
             * @istep 1: 
             * 
             * check if there is Chinese numeric. 
             * make sure chars[cjkidx] is a Chinese numeric
             * and it is not the last word.
             */
            if ( cjkidx + 1 < chars.length 
                    && NumericUtil.isCNNumeric(chars[cjkidx]) > -1 ) {
                //get the Chinese numeric chars
                String num = nextCNNumeric(chars, cjkidx);
                int NUMLEN = num.length();
                
                /*
                 * check the Chinese fraction.
                 * checkCF will be reset to be 'TRUE' it num is a Chinese fraction.
                 * @added 2013-12-14.
                */
                if ( (ctrlMask & ISegment.CHECK_CF_MASK) != 0  ) {
                    //get the Chinese fraction.
                    w = new Word(num, IWord.T_CN_NUMERIC);
                    w.setPosition(pos+cjkidx);
                    w.setPartSpeech(IWord.NUMERIC_POSPEECH);
                    w.setEntity(Entity.E_NUMERIC_CN_FRACTION);
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
                        wd.setEntity(Entity.E_NUMERIC_FRACTION);
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
                            && dic.match(ILexicon.CJK_UNIT, num.substring(NUMLEN)) ) {
                        num     = ONUM;
                        matched = false;    //reset the matched
                    }
                    
                    //find the numeric units
                    IWord wd = null;
                    if ( matched == false && config.CNNUM_TO_ARABIC ) {
                        //get the numeric'a Arabic
                        String arabic = NumericUtil.cnNumericToArabic(num, true)+"";
                        if ( (cjkidx + num.length()) < chars.length
                                && dic.match(ILexicon.CJK_UNIT, chars[cjkidx + num.length()]+"") ) {
                            char units = chars[cjkidx + num.length()];
                            num += units; arabic += units;
                        }
                        
                        wd = new Word(arabic, IWord.T_CN_NUMERIC);
                        wd.setPartSpeech(IWord.NUMERIC_POSPEECH);
                        //wd.setEntity(null);
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
                     * global dictionary accessed with this.dic
                     * 
                     * cuz at the concurrency that will lead to the position error
                     * so, we clone it if the word is directly get from the dictionary
                    */
                    if ( w == null ) {
                        w = new Word(num, IWord.T_CN_NUMERIC);
                        w.setPartSpeech(IWord.NUMERIC_POSPEECH);
                        w.setEntity(Entity.E_NUMERIC_CN);
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
                    w.setEntity(T==IWord.T_CN_NICKNAME ? Entity.E_NAME_NICKNAME : Entity.E_NAME_CN);
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
                    && dic.match(ILexicon.CJK_WORD, cestr) ) {
                    ce = dic.get(ILexicon.CJK_WORD, cestr).clone();
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

}
