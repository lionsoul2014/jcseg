package org.lionsoul.jcseg.filter;

/**
 * a class to deal with the english stop char 
 * 		like the english punctuation. <br />
 * 
 * @author	chenxin<chenxin619315@gmail.com>
 */
public class ENSCFilter 
{
	//type constants
	public static final int EN_LETTER = 0;
	public static final int EN_NUMERIC = 1;
	public static final int EN_PUNCTUATION = 2;
	public static final int EN_WHITESPACE = 3;
	public static final int EN_UNKNOW = -1;
	
	private static final String EN_KEEP_CHARS = "@%&.'#+";
	
	/*private static final Character[] EN_KEEP_CHARS = {
		'@', '$', '%', '^', '&', '-', ':', '.', '/', '\'', '#', '+'};
	
	private static Map<Character, Character> enKeepChar = null;
	
	static {
		enKeepChar = new HashMap<Character, Character>( 
				( int )(EN_KEEP_CHARS.length / 1.7) + 1, 0.85f );
		//set the keep char's keep status
		for ( int j = 0; j < EN_KEEP_CHARS.length; j++ )
			enKeepChar.put(EN_KEEP_CHARS[j], EN_KEEP_CHARS[j]);
	}*/
	
	/**
	 * check the given char is english keep punctuation.
	 * 
	 * @param	c
	 * @return	boolean
	 */
	public static boolean isENKeepPunctuaton( char c ) {
		return (EN_KEEP_CHARS.indexOf(c) > -1);
		//return enKeepChar.containsKey(c);
	}
	
	public static boolean isUpperCaseLetter( int u ) {
		return ( u >= 65 && u <= 90 ); 
	}
	
	public static boolean isLowerCaseLetter( int u ) {
		return ( u >= 97 && u <= 122 );
	}
	
	public static int toLowerCase( int u ) {
		return ( u + 32 );
	}
	
	public static int toUpperCase( int u ) {
		return ( u - 32 );
	}
	
	/**
	 * include the full-width and half-width char.
	 * 
	 * @param	u
	 */
	public static boolean isEnLetter( int u ) {
		if ( u > 65280 ) u -= 65248;			//make full-with half-width
		return ( (u >= 65 && u <= 90) || ( u >= 97 && u <= 122 ) );
	}
	
	/**
	 * get the type of the english char
	 * 	defined in this class and start with EN_. (only half-width)
	 * 
	 * @param	u	char to identity.
	 * @return	int	type keywords. 
	 */
	public static int getEnCharType( int u ) {
		//if ( u > 65280 ) u -= 65248;			//make full-with half-width
		if ( u > 126 )				return EN_UNKNOW;
		if ( u == 32 )				return EN_WHITESPACE;
		if ( u >= 48 && u <= 57 )	return EN_NUMERIC;
		if ( u >= 65 && u <= 90 )	return EN_LETTER;
		if ( u >= 97 && u <= 122 )	return EN_LETTER;
		return EN_PUNCTUATION;
	}
	
	/**
	 * check the given char is a half-width char or not.
	 * 
	 * <ul>
	 * <li>32 		-&gt; whitespace</li>
	 * <li>33-47	-&gt; punctuations</li>
	 * <li>48-57	-&gt; 0-9</li>
	 * <li>58-64	-&gt; punctuations</li>
	 * <li>65-90	-&gt; A-Z</li>
	 * <li>91-96	-&gt; punctuations</li>
	 * <li>97-122	-&gt; a-z</li>
	 * <li>123-126	-&gt; punctuations</li>
	 * </ul>
	 * 
	 * @param int
	 * @return boolean
	 */
    public static boolean isHWEnChar( int c ) {
        return (c >= 32 && c <= 126);
    }
    
    /**
     * check the given char is a full-width char. 
     * @reader: the full-width punctuation is not included here.
     * 
     * @param c
     * @return boolean
     */
    public static boolean isFWEnChar( int c ) {
    	return ( ( c >= 65296 && c <= 65305 )
    			|| ( c >= 65313 && c <= 65338 )
    			|| ( c >= 65345 && c < 65370 ) );
    }
    
    /**
     * check the given char is half-width punctuation.<br />
     * 
     * @param c
     * @return boolean
     */
    public static boolean isEnPunctuation( int c ) {
    	return ( (c > 32 && c < 48) 
    			|| ( c > 57 && c < 65 )
    			|| ( c > 90 && c < 97 ) 
    			|| ( c > 122 && c < 127 ));
    }
    
    public static boolean isCnPunctuation( int c ) {
    	return ( (c > 65280 && c < 65296) 
    			|| ( c > 65305 && c < 65312 )
    			|| ( c > 65338 && c < 65345 ) 
    			|| ( c > 65370 && c < 65382 )
    			//CJK symbol and punctuations (added 2013-09-06)
    			//from http://www.unicode.org/charts/PDF/U3000.pdf
    			|| ( c >= 12289 && c <= 12319 ) );
    }
    
    /**
     * check the given string is a whitespace. <br />
     * 
     * @param c
     * @return boolean;
     */
    public static boolean isWhitespace( int c ) {
    	return ( c == 32 || c == 12288 );
    }
    
	/**
	 * check the specified char is a digit or not.
	 * 		true will return if it is or return false
	 * this method can recognize full-with char.
	 * 
	 * @param	str
	 * @return	boolean
	 */
	public static boolean isDigit( String str ) 
	{
		char c;
		for ( int j = 0; j < str.length(); j++ ) 
		{
			c = str.charAt(j);
			//make full-width char half-width
			if ( c > 65280 ) c -= 65248;
			if ( c < 48 || c > 57 ) return false;
		}
		return true;
	}
	
	/**
	 * check the specified char is a decimal.
	 * 	including the full-width char.
	 * 
	 * @param	str
	 * @return	boolean
	 */
	public static boolean isDecimal( String str ) 
	{
		if ( str.charAt(str.length() - 1) == '.' 
				|| str.charAt(0) == '.' ) return false;
		char c;
		int p= 0;		//number of point
		for ( int j = 1; j < str.length(); j++ ) 
		{
			c = str.charAt(j);
			if ( c == '.' ) p++;
			else 
			{
				//make full-width half-width
				if ( c > 65280 ) c -= 65248;
				if ( c < 48 || c > 57 ) return false;
			}
		}
		
		return (p==1);
	}
    
    /**
     * a static method to replace the full-width char to the half-width char
     * 		in a given string. 
     * (65281-65374 for full-width char) <br />
     * 
     * @param str
     * @return String the new String after the replace.
     */
    public static String fwsTohws( String str ) {
    	char[] chars = str.toCharArray();
    	for ( int j = 0; j < chars.length; j++ ) {
    		if ( chars[j] == '\u3000' )
    			chars[j] = '\u0020';
    		else if ( chars[j] > '\uFF00' && chars[j] < '\uFF5F' ) 
    			chars[j] = ( char ) (chars[j] - 65248);
    	}
    	return new String(chars);
    }
	
    /**
     * a static method to replace the half-width char to the full-width char.
     * 		in a given string. <br />
     * 
     * @param str
     * @return String the new String after the replace.
     */
    public static String hwsTofws( String str ) {
    	char[] chars = str.toCharArray();
    	for ( int j = 0; j < chars.length; j++ ) {
    		if ( chars[j] == '\u0020' ) 
    			chars[j] = '\u3000';
    		else if ( chars[j] < '\177' )
    			chars[j] = ( char ) ( chars[j] + 65248 );
    	}
    	return new String(chars);
    }
}
