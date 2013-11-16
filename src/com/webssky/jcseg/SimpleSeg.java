package com.webssky.jcseg;

import java.io.IOException;
import java.io.Reader;

import com.webssky.jcseg.core.ADictionary;
import com.webssky.jcseg.core.IChunk;
import com.webssky.jcseg.core.IWord;
import com.webssky.jcseg.core.JcsegTaskConfig;

/**
 * simplex segment for JCSeg,
 * 		has extend from ASegment. <br />
 * 
 * @author	chenxin<chenxin619315@gmail.com>
 */
public class SimpleSeg extends ASegment {
	
	public SimpleSeg( JcsegTaskConfig config, ADictionary dic ) throws IOException {
		super(config, dic);
	}
	
	public SimpleSeg( Reader input, 
			JcsegTaskConfig config, ADictionary dic ) throws IOException {
		super(input, config, dic);
	}

	/**
	 * @see ASegment#getBestCJKChunk(char[], int) 
	 */
	@Override
	public IChunk getBestCJKChunk(char[] chars, int index) {
		IWord[] words = getNextMatch(chars, index);
		return new Chunk(new IWord[]{words[words.length - 1]});
	}

}
