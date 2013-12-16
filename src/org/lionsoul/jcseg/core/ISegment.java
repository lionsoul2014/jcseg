package org.lionsoul.jcseg.core;

import java.io.IOException;
import java.io.Reader;

/**
 * Jcseg segment interface
 * 
 * @author	chenxin<chenxin619315@gmail.com>
 */
public interface ISegment {
	
	//Wether to check the chinese and english mixed word.
	public static final int CHECK_CE_MASk = 1 << 0;
	//Wether to check the chinese fraction.
	public static final int CHECK_CF_MASK = 1 << 1;
	//Wether to start the latin secondary segmentation.
	public static final int START_SS_MASK = 1 << 2;
	
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
	 * 		from a specified position.
	 * 
	 * @return IWord
	 */
	public IWord next() throws IOException;
}
