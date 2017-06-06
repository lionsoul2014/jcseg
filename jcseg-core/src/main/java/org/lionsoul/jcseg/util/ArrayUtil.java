package org.lionsoul.jcseg.util;

/**
 * Array util class
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class ArrayUtil
{
    /**
     * String array implode internal method
     * 
     * @param   glue
     * @param   pieces
     * @return  String
    */
    public static String implode(String glue, Object[] pieces)
    {
        if ( pieces == null ) {
            return null;
        }
        
        StringBuffer sb = new StringBuffer();
        for ( Object o : pieces ) {
            if ( sb.length() > 0 ) {
                sb.append(glue);
            }
            
            sb.append(o.toString());
        }
        
        return sb.toString();
    }
    
    /**
     * check and search the specified element in the Array 
     * 
     * @param   ele
     * @param   arr
     * @return  int
    */
    public static int indexOf(String ele, String[] arr)
    {
        if ( arr == null ) {
            return -1;
        }
        
        for ( int i = 0; i < arr.length; i++ ) {
            if ( arr[i].equals(ele) ) {
                return i;
            }
        }
        
        return -1;
    }
    
    /**
     * check if there is a element that start with the specified string
     * 
     * @param   str
     * @return  int
    */
    public static int startsWith(String str, String[] arr)
    {
        if ( arr == null ) {
            return -1;
        }
        
        for ( int i = 0; i < arr.length; i++ ) {
            if ( arr[i].startsWith(str) ) {
                return i;
            }
        }
        
        return -1;
    }
}
