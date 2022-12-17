package org.lionsoul.jcseg.dic;

/**
 * lexicon configuration class.
 * 
 * @author  chenxin<chenxin619315@gmail.com>
 */
public interface ILexicon
{
    int T_LEN = 11;
    
    /**
     * Chinese, Japanese, Korean words
     * 
     * Note: since version 2.0.1 the Chinese English mixed words
     * and the English punctuation mixed words are all included here.
     * implemented at 2016/11/09
    */
    int CJK_WORD = 0;
    
    /**
     * Chinese single units 
     */
    int CJK_UNIT = 1;
    
    /**
     * Chinese last name
     */
    int CN_LNAME = 2;
    
    /**
     * Chinese single name
     */
    int CN_SNAME = 3;
    
    /**
     * first word of Chinese double name
     */
    int CN_DNAME_1 = 4;
    
    /**
     * second word of Chinese double name
     */
    int CN_DNAME_2 = 5;
    
    /**
     * the adorn(修饰) char before the last name
     * like word "老陈", "小陈"
    */
    int CN_LNAME_ADORN = 6;
    
    /**
     * stop words 
    */
    int STOP_WORD = 7;
    
    /**
     * special lexicon for Chinese-English[-Chinese] mixed word recognition
     * For the optimization implementation of the mixed word recognition
    */
    int MIX_ASSIST_WORD = 8;
    
    /**
     * domain name suffix dictionary for the URL recognition
    */
    int DOMAIN_SUFFIX = 9;
    
    int NUMBER_UNIT = 10;
    
    
    /**
     * CJK single word
    */
    int CJK_CHAR = 15;
    
    /**
     * CJK synonyms
    */
    int CJK_SYN = 16;
    
    /**
     * unmatched word
     */
    int UNMATCH_CJK_WORD = 17;

}
