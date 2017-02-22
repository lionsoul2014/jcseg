package org.lionsoul.jcseg.util;

import java.util.Arrays;

/**
 * All Basic printable Latin char counter class
 * include all the English punctuation and the letters
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class ByteCharCounter
{
    /**
     * byte buffer only count the char ' '[=32] to char '~'[=126]
    */
    private final byte[] buffer = new byte[95];
    
    public ByteCharCounter()
    {
        Arrays.fill(buffer, (byte)0);
    }
    
    /**
     * check whether the current char is valid for counter
     * 
     * @param chr
    */
    private boolean isValidChar(char chr)
    {
        if ( chr < 32 || chr > 126 ) {
            return false;
        }
        
        return true;
    }
    
    public ByteCharCounter increase(char chr) {return increase(chr, 1);};
    public ByteCharCounter increase(char chr, int val)
    {
        if ( isValidChar(chr) ) {
            buffer[chr-32] += val;
        }
        return this;
    }
    
    public ByteCharCounter decrease(char chr) {return decrease(chr, 1);}
    public ByteCharCounter decrease(char chr, int val)
    {
        int idx = chr - 32;
        if ( isValidChar(chr) && buffer[idx] > 0 ) {
            buffer[idx]--;
        }
        return this;
    }
    
    public int get(char chr)
    {
        return isValidChar(chr) ? buffer[chr-32] : 0;
    }
    
    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < buffer.length; i++ ) {
            char chr = (char)(i + 32);
            int cunt = get(chr);
            if ( cunt > 0 ) {
                sb.append(chr).append(':').append(get(chr)).append('\n');
            }
        }
        return sb.toString();
    }
    
}
