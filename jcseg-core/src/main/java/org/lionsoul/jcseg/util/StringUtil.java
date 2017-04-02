package org.lionsoul.jcseg.util;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>a class to deal with the English stop char like the English punctuation</p>
 * 
 * @author  chenxin<chenxin619315@gmail.com>
 */
public class StringUtil 
{
    //type constants
    public static final int EN_LETTER = 0;
    public static final int EN_NUMERIC = 1;
    public static final int EN_PUNCTUATION = 2;
    public static final int EN_WHITESPACE = 3;
    public static final int EN_UNKNOW = -1;
    
    /**
     * keep punctuation set for NLP segmentation currently 
    */
    private static final String EN_KEEP_PUNCS = "@/-_=?%&.'#+:";
    
    /**
     * Need to be cleared tailing punctuation set
    */
    private static final String EN_NO_TAILING_PUNCS = ".?@:'";
    
    /**
     * check the specified char is CJK, Thai... char
     * true will be return if it is or return false
     * 
     * @param c
     * @return boolean
     */
    public static boolean isCJKChar( int c ) 
    {
        /*
         * @Note: added at 2015-11-25
         * for foreign country translated name recognize
         * add '·' as CJK chars
        */
        if ( c == 183 
                || Character.getType(c) == Character.OTHER_LETTER )
            return true;
        return false;
    }
    
    /**
     * check the specified char is a basic Latin and Russia and 
     * Greece letter. True will be return if it is or return false.
     * this method can recognize full-width char and letter
     * 
     * @param c
     * @return boolean
     */
    public static boolean isEnChar( int c ) 
    {
        /*int type = Character.getType(c);
        Character.UnicodeBlock cu = Character.UnicodeBlock.of(c);
        if ( ! Character.isWhitespace(c) && 
                (cu == Character.UnicodeBlock.BASIC_LATIN
                || type == Character.DECIMAL_DIGIT_NUMBER
                || type == Character.LOWERCASE_LETTER
                || type == Character.UPPERCASE_LETTER
                || type == Character.TITLECASE_LETTER
                || type == Character.MODIFIER_LETTER)) 
            return true;
        return false;*/
        return ( StringUtil.isHWEnChar(c) || StringUtil.isFWEnChar(c) );
    }
    
    /**
     * check the specified char is Letter number like 'ⅠⅡ'
     * true will be return if it is, or return false
     * 
     * @param c
     * @return boolean
     */
    public static boolean isLetterNumber( int c ) 
    {
        if ( Character.getType(c) == Character.LETTER_NUMBER ) 
            return true;
        return false;
    }
    
    /**
     * check the specified char is other number like '①⑩⑽㈩'
     * true will be return if it is, or return false
     * 
     * @param c
     * @return boolean
     */
    public static boolean isOtherNumber( int c ) 
    {
        if ( Character.getType(c) == Character.OTHER_NUMBER ) 
            return true;
        return false;
    }
    
    /**
     * check the given char is English keep punctuation
     * 
     * @param    c
     * @return  boolean
     */
    public static boolean isENKeepPunctuaton( char c )
    {
        return (EN_KEEP_PUNCS.indexOf(c) > -1);
    }
    
    /**
     * check if the given punctuation is the one that need to be cleared
     * 
     * @param   c
     * @return  boolean
    */
    public static boolean isNoTailingPunctuation(char c)
    {
        return (EN_NO_TAILING_PUNCS.indexOf(c) > -1);
    }
    
    public static boolean isUpperCaseLetter( int u )
    {
        return ( u >= 65 && u <= 90 ); 
    }
    
    public static boolean isLowerCaseLetter( int u )
    {
        return ( u >= 97 && u <= 122 );
    }
    
    public static int toLowerCase( int u )
    {
        return ( u + 32 );
    }
    
    public static int toUpperCase( int u )
    {
        return ( u - 32 );
    }
    
    /**
     * include the full-width and half-width char
     * 
     * @param   u
     * @return  boolean
     */
    public static boolean isEnLetter( int u )
    {
        if ( u > 65280 ) u -= 65248;            //make full-with half-width
        return ( (u >= 65 && u <= 90) || ( u >= 97 && u <= 122 ) );
    }
    
    /**
     * check the specified char is an English numeric(48-57)
     * including the full-width char
     *  
     * @param   u
     * @return  boolean
    */
    public static boolean isEnNumeric( int u )
    {
        if ( u > 65280 ) u -= 65248;            //make full-with half-width
        return ( (u >= 48 && u <= 57) );
    }
    
    /**
     * get the type of the English char
     * defined in this class and start with EN_. (only half-width)
     * 
     * @param   u char to identity
     * @return  int type keywords
     */
    public static int getEnCharType( int u )
    {
        //if ( u > 65280 ) u -= 65248;            //make full-with half-width
        if ( u > 126 )                return EN_UNKNOW;
        if ( u == 32 )              return EN_WHITESPACE;
        if ( u >= 48 && u <= 57 )    return EN_NUMERIC;
        if ( u >= 65 && u <= 90 )    return EN_LETTER;
        if ( u >= 97 && u <= 122 )    return EN_LETTER;
        return EN_PUNCTUATION;
    }
    
    /**
     * <p>
     * check the given char is a half-width char or not
     * </p>
     * 
     * <ul>
     * <li>32       -&gt; whitespace</li>
     * <li>33-47    -&gt; punctuation</li>
     * <li>48-57    -&gt; 0-9</li>
     * <li>58-64    -&gt; punctuation</li>
     * <li>65-90    -&gt; A-Z</li>
     * <li>91-96    -&gt; punctuation</li>
     * <li>97-122   -&gt; a-z</li>
     * <li>123-126  -&gt; punctuation</li>
     * </ul>
     * 
     * @param c
     * @return boolean
     */
    public static boolean isHWEnChar( int c )
    {
        return (c >= 32 && c <= 126);
    }
    
    /**
     * check the given char is a full-width char
     * AT+reader: the full-width punctuation is not included here
     * 
     * @param c
     * @return boolean
     */
    public static boolean isFWEnChar( int c )
    {
        return ( ( c >= 65296 && c <= 65305 )
                || ( c >= 65313 && c <= 65338 )
                || ( c >= 65345 && c < 65370) );
    }
    
    /**
     * check the given char is half-width punctuation
     * 
     * @param c
     * @return boolean
     */
    public static boolean isEnPunctuation( int c )
    {
        return ( (c > 32 && c < 48) 
                || ( c > 57 && c < 65 )
                || ( c > 90 && c < 97 ) 
                || ( c > 122 && c < 127 )
        );
    }
    
    public static boolean isCnPunctuation( int c )
    {
        return ( (c > 65280 && c < 65296) 
                || ( c > 65305 && c < 65312 )
                || ( c > 65338 && c < 65345 ) 
                || ( c > 65370 && c < 65382 )
                //CJK symbol and punctuation (added 2013-09-06)
                //from http://www.unicode.org/charts/PDF/U3000.pdf
                || ( c >= 12289 && c <= 12319 )
         );
    }
    
    /**
     * check the given string is a whitespace
     * 
     * @param c
     * @return boolean
     */
    public static boolean isWhitespace( int c )
    {
        return ( c == 32 || c == 12288 );
    }
    
    /**
     * check the specified char is a digit or not
     * true will return if it is or return false this method can recognize full-with char
     * 
     * @param   str
     * @param   beginIndex
     * @param   endIndex
     * @return  boolean
    */
    public static boolean isDigit(String str) {return isDigit(str, 0, str.length());}
    public static boolean isDigit(String str, int beginIndex, int endIndex) 
    {
        char c;
        for ( int j = beginIndex; j < endIndex; j++ ) {
            c = str.charAt(j);
            //make full-width char half-width
            if ( c > 65280 ) c -= 65248;
            if ( c < 48 || c > 57 ) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * check the specified char is a decimal including the full-width char
     * 
     * @param   str
     * @param   beginIndex
     * @param   endIndex
     * @return  boolean
    */
    public static boolean isDecimal(String str) {return isDecimal(str, 0, str.length());}
    public static boolean isDecimal(String str, int beginIndex, int endIndex) 
    {
        if ( str.charAt(str.length() - 1) == '.' 
                || str.charAt(0) == '.' ) {
            return false;
        }
        
        char c;
        int p= 0;        //number of point
        for ( int j = 1; j < str.length(); j++ ) {
            c = str.charAt(j);
            if ( c == '.' ) {
                p++;
            } else {
                //make full-width half-width
                if ( c > 65280 ) c -= 65248;
                if ( c < 48 || c > 57 ) return false;
            }
        }
        
        return (p==1);
    }
    
    /**
     * check if the specified string is all Latin chars
     * 
     * @param   str
     * @param   beginIndex
     * @param   endIndex
     * @return  boolean
    */
    public static boolean isLatin(String str) {return isLatin(str, 0, str.length());}
    public static boolean isLatin(String str, int beginIndex, int endIndex)
    {
        for ( int j = beginIndex; j < endIndex; j++ ) {
            if ( ! isEnChar(str.charAt(j)) ) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * check if the specified string is all CJK chars
     * 
     * @param   str
     * @param   beginIndex
     * @param   endIndex
     * @return  boolean
    */
    public static boolean isCJK(String str) {return isCJK(str, 0, str.length());}
    public static boolean isCJK(String str, int beginIndex, int endIndex)
    {
        for ( int j = beginIndex; j < endIndex; j++ ) {
            if ( ! isCJKChar(str.charAt(j)) ) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * check if the specified string is Latin numeric or letter
     * 
     * @param   str
     * @param   beginIndex
     * @param   endIndex
     * @return  boolean
    */
    public static boolean isLetterOrNumeric(String str) {return isLetterOrNumeric(str, 0, str.length());}
    public static boolean isLetterOrNumeric(String str, int beginIndex, int endIndex)
    {
        for ( int i = beginIndex; i < endIndex; i++ ) {
            char chr = str.charAt(i);
            if ( ! StringUtil.isEnLetter(chr) 
                    && ! StringUtil.isEnNumeric(chr) ) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * check if the specified string is Latin letter
     * 
     * @param   str
     * @param   beginIndex
     * @param   endIndex
     * @return  boolean
    */
    public static boolean isLetter(String str) {return isLetter(str, 0, str.length());} 
    public static boolean isLetter(String str, int beginIndex, int endIndex) 
    {
        for ( int i = beginIndex; i < endIndex; i++ ) {
            char chr = str.charAt(i);
            if ( ! StringUtil.isEnLetter(chr) ) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * check if the specified string it Latin numeric
     * 
     * @param   str
     * @param   beginIndex
     * @param   endIndex
     * @return  boolean
    */
    public static boolean isNumeric(String str) {return isNumeric(str, 0, str.length());}
    public static boolean isNumeric(String str, int beginIndex, int endIndex)
    {
        for ( int i = beginIndex; i < endIndex; i++ ) {
            char chr = str.charAt(i);
            if ( ! StringUtil.isEnNumeric(chr) ) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * get the index of the first Latin char of the specified string
     * 
     * @param   str
     * @param   offset
     * @return  integer
    */
    public static int latinIndexOf(String str, int offset)
    {
        for ( int j = offset; j < str.length(); j++ ) {
            if ( isEnChar(str.charAt(j)) ) {
                return j;
            }
        }
        
        return -1;
    }
    
    public static int latinIndexOf(String str)
    {
        return latinIndexOf(str, 0);
    }
    
    /**
     * get the index of the first CJK char of the specified string
     * 
     * @param   str
     * @param   offset
     * @return  integer
    */
    public static int CJKIndexOf(String str, int offset)
    {
        for ( int j = offset; j < str.length(); j++ ) {
            if ( isCJKChar(str.charAt(j)) ) {
                return j;
            }
        }
        
        return -1;
    }
    
    public static int CJKIndexOf(String str)
    {
        return CJKIndexOf(str, 0);
    }
    
    /**
     * a static method to replace the full-width char to the half-width char in a given string
     * (65281-65374 for full-width char)
     * 
     * @param str
     * @return String the new String after the replace.
     */
    public static String fwsTohws( String str )
    {
        char[] chars = str.toCharArray();
        for ( int j = 0; j < chars.length; j++ ) {
            if ( chars[j] == '\u3000' ) {
                chars[j] = '\u0020';
            } else if ( chars[j] > '\uFF00' 
                && chars[j] < '\uFF5F' ) {
                chars[j] = (char)(chars[j] - 65248);
            }       
        }
        
        return new String(chars);
    }
    
    /**
     * a static method to replace the half-width char to the full-width char in a given string
     * 
     * @param str
     * @return String the new String after the replace
     */
    public static String hwsTofws( String str )
    {
        char[] chars = str.toCharArray();
        for ( int j = 0; j < chars.length; j++ ) {
            if ( chars[j] == '\u0020' ) {
                chars[j] = '\u3000';
            } else if ( chars[j] < '\177' ) {
                chars[j] = (char)(chars[j] + 65248);
            }
        }
        
        return new String(chars);
    }
    
    private static final Character[] PAIR_PUNCTUATION = {
        /*'“', '”', '‘', '’',*/ '《', '》', '『', '』', '【', '】'};
    private static Map<Character, Character> pairPunctuation = null;
    
    static {
        pairPunctuation = new HashMap<Character, Character>( 
                (int)(PAIR_PUNCTUATION.length / 1.7) + 1, 0.85f);
        for ( int j = 0; j < PAIR_PUNCTUATION.length; j += 2 ) {
            pairPunctuation.put(PAIR_PUNCTUATION[j], PAIR_PUNCTUATION[j+1]);
        }
    }
    
    /**
     * check the given char is pair punctuation or not
     * 
     * @param c
     * @return boolean true for it is and false for not
     */
    public static boolean isPairPunctuation( char c )
    {
        return pairPunctuation.containsKey(c);
    }
    
    /**
     * get the pair punctuation' pair
     * 
     * @param c
     * @return char
     */
    public static char getPunctuationPair( char c )
    {
        return pairPunctuation.get(c);
    }
    
}
