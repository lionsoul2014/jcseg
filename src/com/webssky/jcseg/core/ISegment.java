package com.webssky.jcseg.core;

import java.io.IOException;
import java.io.Reader;

/**
 * Jcseg segment interface
 * 
 * @author	chenxin<chenxin619315@gmail.com>
 */
public interface ISegment {
	
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
