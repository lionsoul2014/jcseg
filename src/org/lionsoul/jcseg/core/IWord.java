package org.lionsoul.jcseg.core;

/**
 * Word interface
 * 
 * @author	chenxin<chenxin619315@gmail.com>
 */
public interface IWord {
	
	public static final String[] NAME_POSPEECH = {"nr"};
	public static final String[] NUMERIC_POSPEECH = {"m"};
	public static final String[] EN_POSPEECH = {"en"};
	public static final String[] MIX_POSPEECH = {"mix"};
	public static final String[] PPT_POSPEECH = {"nz"};
	public static final String[] PUNCTUATION = {"w"};
	public static final String[] UNRECOGNIZE = {"urg"};
			
	/**
	 * China,JPanese,Korean words 
	 */
	public static final int T_CJK_WORD = 1;
	
	/**
	 * chinese and english mix word.
	 * 		like B超,SIM卡. 
	 */
	public static final int T_MIXED_WORD = 2;
	
	/**
	 * chinese last name. 
	 */
	public static final int T_CN_NAME = 3;
	
	/**
	 * chinese nickname.
	 * like: 老陈 
	 */
	public static final int T_CN_NICKNAME = 4;
	
	/**
	 * latain series.
	 * 	including the arabic numbers.
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
	 * pinyin 
	 */
	public static final int T_CJK_PINYIN = 8;
	
	/**
	 * Chinese numeric */
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
	String getValue();
	
	/**
	 * return the length of the word
	 * 
	 * @return int
	 */
	int getLength();
	
	/**
	 * return the frequency of the word,
	 * 	use only when the word's length is one.
	 * 
	 * @return int
	 */
	int getFrequency();
	
	/**
	 * return the type of the word
	 * 
	 * @return int
	 */
	int getType();
	
	/**
	 * set the position of the word
	 * 
	 * @param pos
	 */
	void setPosition( int pos );
	
	/**
	 * return the start position of the word.
	 * 
	 * @return int
	 */
	int getPosition();
	
	/**
	 * return the pinying of the word 
	 */
	public String getPinyin();
	
	/**
	 * return the syn words of the word.
	 * 
	 * @return String[]
	 */
	public String[] getSyn();
	
	public void setSyn( String[] syn );
	
	/**
	 * return the part of speech of the word.
	 * 
	 * @return String[]
	 */
	public String[] getPartSpeech();
	
	public void setPartSpeech( String[] ps );
	
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
	 * add a new syn word to the word.
	 * 
	 * @param s
	 */
	public void addSyn( String s );
}
