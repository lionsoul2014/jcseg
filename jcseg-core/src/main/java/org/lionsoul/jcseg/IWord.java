package org.lionsoul.jcseg;

/**
 * Word Token interface
 * 
 * @author	chenxin<chenxin619315@gmail.com>
 */
public interface IWord extends Cloneable
{    
    public static final String[] NAME_POSPEECH = {"nr"};
    public static final String[] NUMERIC_POSPEECH = {"m"};
    public static final String[] TIME_POSPEECH = {"t"};
    public static final String[] EN_POSPEECH  = {"en"};
    public static final String[] MIX_POSPEECH = {"mix"};
    public static final String[] PPT_POSPEECH = {"nz"};
    public static final String[] PUNCTUATION  = {"w"};
    public static final String[] QUANTIFIER = {"q"};
    public static final String[] UNRECOGNIZE  = {"urg"};
            
    /**
     * China,JPanese,Korean words 
     */
    public static final int T_CJK_WORD = 1;
    
    /**
     * Chinese and English mix word like B超,SIM卡. 
     */
    public static final int T_MIXED_WORD = 2;
    
    /**
     * Chinese last name. 
     */
    public static final int T_CN_NAME = 3;
    
    /**
     * Chinese nickname like: 老陈 
     */
    public static final int T_CN_NICKNAME = 4;
    
    /**
     * Latin series.
     * including the Arabic numbers.
     */
    public static final int T_BASIC_LATIN = 5;
    
    /**
     * letter number like 'ⅠⅡ' 
     */
    public static final int T_LETTER_NUMBER = 6;
    
    /**
     * other number like '①⑩⑽㈩' 
     */
    public static final int T_OTHER_NUMBER = 7;
    
    /**
     * Pinyin 
     */
    public static final int T_CJK_PINYIN = 8;
    
    /**
     * Chinese numeric
    */
    public static final int T_CN_NUMERIC = 9;
    
    public static final int T_PUNCTUATION = 10;
    
    /**
     * useless chars like the CJK punctuation
     */
    public static final int T_UNRECOGNIZE_WORD = 11;
    
    /**
     * return the value of the word
     * 
     * @return String
     */
    public String getValue();
    
    /**
     * return the length of the word
     * 
     * @return int
     */
    public int getLength();
    
    /**
     * self define the length
     * 
     * @param    length
     */
    public void setLength( int length );
    
    /**
     * return the frequency of the word,
     *     use only when the word's length is one.
     * 
     * @return int
     */
    public int getFrequency();
    
    /**
     * return the type of the word
     * 
     * @return int
     */
    public int getType();
    
    /**
     * set the type of the word
     * 
     * @param	type
    */
    public void setType(int type);
    
    /**
     * set the position of the word
     * 
     * @param pos
     */
    public void setPosition( int pos );
    
    /**
     * return the start position of the word.
     * 
     * @return int
     */
    public int getPosition();
    
    /**
     * get the entity name array of the word
     * 
     * @return  String[]
    */
    public String[] getEntity();
    
    /**
     * get the specified entity by index
     * 
     * @param   idx
     * @return  String
    */
    public String getEntity(int idx);
    
    /**
     * set the entity name array of the word
     * 
     * @param   entity
    */
    public void setEntity(String[] entity);
    public void setEntityForNull(String[] entity);
    
    /**
     * append a new entity
     * 
     * @param   e
    */
    public void addEntity(String e);
    
    /**
     * return the pinying of the word 
     */
    public String getPinyin();
    
    /**
     * return the synonyms entry of the word.
     * 
     * @return SynonymsEntry
     */
    public SynonymsEntry getSyn();
    
    public void setSyn( SynonymsEntry syn );
    
    /**
     * return the part of speech of the word.
     * 
     * @return String[]
     */
    public String[] getPartSpeech();
    
    public void setPartSpeech( String[] ps );
    
    public void setPartSpeechForNull( String[] ps );
    
    /**
     * set the pinying of the word
     * 
     * @param py
     */
    public void setPinyin( String py );
    
    /**
     * add a new part to speech to the word.
     * 
     * @param ps
     */
    public void addPartSpeech( String ps );
    
    /**
     * return the additional parameters
     * 
     * @return  String
    */
    public String getParameter();
    
    /**
     * set the string parameters
     * 
     * @param   param
    */
    public void setParameter(String param);
    
    /**
     * I mean: you have to rewrite the equals method
     *  cause the Jcseg require it 
     */
    @Override
    public boolean equals( Object o );
    
    /**
     * make clone available
     */
    public IWord clone();
}
