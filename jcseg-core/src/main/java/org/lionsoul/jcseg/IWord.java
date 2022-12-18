package org.lionsoul.jcseg;

/**
 * Word Token interface
 * 
 * @author	chenxin<chenxin619315@gmail.com>
 */
public interface IWord extends Cloneable
{    
    String[] NAME_POSPEECH = {"nr"};
    String[] NUMERIC_POSPEECH = {"m"};
    String[] TIME_POSPEECH = {"t"};
    String[] EN_POSPEECH  = {"en"};
    String[] MIX_POSPEECH = {"mix"};
    String[] PPT_POSPEECH = {"nz"};
    String[] PUNCTUATION  = {"w"};
    String[] QUANTIFIER = {"q"};
    String[] UNRECOGNIZE  = {"urg"};
            
    /** Chinese,Japanese,Korean words */
    int T_CJK_WORD = 1;
    
    /** Chinese and English mix word like B超,SIM卡. */
    int T_MIXED_WORD = 2;
    
    /** Chinese last name. */
    int T_CN_NAME = 3;
    
    /** Chinese nickname like: 老陈 */
    int T_CN_NICKNAME = 4;
    
    /** Latin series including the Arabic numbers. */
    int T_BASIC_LATIN = 5;
    
    /** letter number like 'ⅠⅡ' */
    int T_LETTER_NUMBER = 6;
    
    /** other number like '①⑩⑽㈩' */
    int T_OTHER_NUMBER = 7;
    
    /** Chinese Pinyin */
    int T_CJK_PINYIN = 8;
    
    /** Chinese numeric */
    int T_CN_NUMERIC = 9;
    
    int T_PUNCTUATION = 10;
    
    /** useless chars like the CJK punctuation */
    int T_UNRECOGNIZE_WORD = 11;
    
    /** return the value of the word */
    String getValue();
    
    /** return the length of the word */
    int getLength();
    
    /** self define the length */
    void setLength(int length);
    
    /** return the frequency of the word, use only when the word's length is one. */
    int getFrequency();

    /** set the frequency of the word */
    void setFrequency(int freq);

    /** return the type of the word */
    int getType();
    
    /** set the type of the word */
    void setType(int type);
    
    /** set the position of the word */
    void setPosition(int pos);
    
    /** return the start position of the word. */
    int getPosition();
    
    /** get the entity name array of the word */
    String[] getEntity();
    
    /** get the specified entity by index */
    String getEntity(int idx);
    
    /** set the entity name array of the word */
    void setEntity(String[] entity);
    void setEntityForNull(String[] entity);
    
    /** append a new entity */
    void addEntity(String e);
    
    /** return the pinyin of the word */
    String getPinyin();
    
    /** return the Synonyms' entry of the word. */
    SynonymsEntry getSyn();
    
    void setSyn(SynonymsEntry syn);
    
    /** return the part of speech of the word. */
    String[] getPartSpeech();
    
    void setPartSpeech(String[] ps);
    
    void setPartSpeechForNull(String[] ps);
    
    /** set the pinyin of the word */
    void setPinyin(String py);
    
    /** add a new part to speech to the word. */
    void addPartSpeech(String ps);
    
    /** return the additional parameters */
    String getParameter();
    
    /** set the string parameters */
    void setParameter(String param);
    
    /** you have to rewrite the #equals method cause the Jcseg require it */
    @Override
    boolean equals(Object o);
    
    /** make clone available */
    IWord clone();
}
