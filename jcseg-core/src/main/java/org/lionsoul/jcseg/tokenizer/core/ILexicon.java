package org.lionsoul.jcseg.tokenizer.core;

/**
 * lexicon configuration class.
 * 
 * @author  chenxin<chenxin619315@gmail.com>
 */
public interface ILexicon
{
    public static final int T_LEN = 10;
    
    /**
     * Chinese, JPanese, Korean words
     * 
     * @Note since version 2.0.1 the Chinese English mixed words
     * and the English punctuation mixed words are all included here.
     * implemented at 2016/11/09
    */
    public static final int CJK_WORD = 0;
    
    /**
     * Chinese single units 
     */
    public static final int CJK_UNIT = 1;
    
    /**
     * Chinese last name
     */
    public static final int CN_LNAME = 2;
    
    /**
     * Chinese single name
     */
    public static final int CN_SNAME = 3;
    
    /**
     * first word of Chinese double name
     */
    public static final int CN_DNAME_1 = 4;
    
    /**
     * second word of Chinese double name
     */
    public static final int CN_DNAME_2 = 5;
    
    /**
     * the adorn(修饰) char before the last name
     * like word "老陈", "小陈"
    */
    public static final int CN_LNAME_ADORN = 6;
    
    /**
     * stop words 
    */
    public static final int STOP_WORD = 7;
    
    /**
     * special lexicon for Chinese-English[-Chinese] mixed word recognition
     * For the optimization implementation of the mixed word recognition
    */
    public static final int MIX_ASSIST_WORD = 8;
    
    /**
     * domain name suffix dictionary for the URL recognition
    */
    public static final int DOMAIN_SUFFIX = 9;
    
    
    /**
     * CJK single word
    */
    public static final int CJK_CHAR = 11;
    
    /**
     * unmatched word
     */
    public static final int UNMATCH_CJK_WORD = 12;
}
