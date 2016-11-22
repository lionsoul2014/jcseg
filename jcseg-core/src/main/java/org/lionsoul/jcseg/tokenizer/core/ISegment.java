package org.lionsoul.jcseg.tokenizer.core;

import java.io.IOException;
import java.io.Reader;

/**
 * Jcseg segment interface
 * 
 * @author  chenxin<chenxin619315@gmail.com>
 */
public interface ISegment 
{
    //Whether to check the Chinese and English mixed word.
    public static final int CHECK_CE_MASk = 1 << 0;
    //Whether to check the Chinese fraction.
    public static final int CHECK_CF_MASK = 1 << 1;
    //Whether to start the Latin secondary segmentation.
    public static final int START_SS_MASK = 1 << 2;
    /**
     * Whether to check the English Chinese mixed suffix
     * For the new implementation of the mixed word recognition 
     * 
     * Added at 2016/11/22
    */
    public static final int CHECK_EC_MASK = 1 << 3;
    
    /**
     * reset the reader
     * 
     * @param input
     */
    public void reset( Reader input ) throws IOException;
    
    /**
     * get the current length of the stream
     * 
     * @return int
     */
    public int getStreamPosition();
    
    /**
     * segment a word from a char array
     *         from a specified position.
     * 
     * @return IWord
     */
    public IWord next() throws IOException;
}
