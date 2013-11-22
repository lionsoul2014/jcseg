package org.lionsoul.jcseg;

import java.util.ArrayList;

import org.lionsoul.jcseg.core.IChunk;
import org.lionsoul.jcseg.core.IRule;


/**
 * the second filter rule
 * 		- largest average word length.
 * this rule will return the chunks that own
 * the largest average word length.
 * 
 * @author	chenxin<chenxin619315@gmail.com>
 */
public class LAWLRule implements IRule {
	
	/**
	 * maxmum match rule instance.
	 */
	private static LAWLRule __instance = null;
	
	/**
	 * return the quote to the maximum match instance.
	 * 
	 * @return MMRule
	 */
	public static LAWLRule createRule() {
		if ( __instance == null )
			__instance = new LAWLRule();
		return __instance;
	}
	
	private LAWLRule() {}

	/**
	 * interface for largest average word length.
	 * 
	 * @see IRule#call(IChunk[])
	 */
	@Override
	public IChunk[] call(IChunk[] chunks) {
		
		double largetAverage = chunks[0].getAverageWordsLength();
		int j;
		
		//find the largest average word length
		for ( j = 1; j < chunks.length; j++ ) {
			if ( chunks[j].getAverageWordsLength() > largetAverage ) 
				largetAverage = chunks[j].getAverageWordsLength();
		}
		
		//get the items that the average word length equals to
		//the max's.
		ArrayList<IChunk> chunkArr = new ArrayList<IChunk>(chunks.length);
		for ( j = 0; j < chunks.length; j++ ) {
			if ( chunks[j].getAverageWordsLength() == largetAverage)
				chunkArr.add(chunks[j]);
		}
		
		IChunk[] lchunk = new IChunk[chunkArr.size()];
		chunkArr.toArray(lchunk);
		chunkArr.clear();
		
		return lchunk;
	}

}
