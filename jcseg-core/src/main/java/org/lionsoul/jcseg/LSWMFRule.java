package org.lionsoul.jcseg;

import java.util.ArrayList;

import org.lionsoul.jcseg.core.IChunk;
import org.lionsoul.jcseg.core.IRule;


/**
 * the fouth filter rule
 * 		-the largest sum of degree of morphemic freedom 
 * 		of one-character words.
 * this rule will return the chunks that own
 * the largest sum of degree of morphemic freedom of one-character.
 * 
 * @author	chenxin<chenxin619315@gmail.com>
 */
public class LSWMFRule implements IRule {
	
	/**
	 * maxmum match rule instance.
	 */
	private static LSWMFRule __instance = null;
	
	/**
	 * return the quote to the maximum match instance.
	 * 
	 * @return MMRule
	 */
	public static LSWMFRule createRule() {
		if ( __instance == null )
			__instance = new LSWMFRule();
		return __instance;
	}
	
	private LSWMFRule() {}

	/**
	 * largest single word morphemic freedom.
	 * 
	 * @see IRule#call(IChunk[])
	 */
	@Override
	public IChunk[] call(IChunk[] chunks) {
		
		double largestFreedom = chunks[0].getSingleWordsMorphemicFreedom();
		int j;
		
		//find the maximum sum of single morphemic freedom
		for ( j = 1; j < chunks.length; j++ ) {
			if ( chunks[j].getSingleWordsMorphemicFreedom() > largestFreedom ) 
				largestFreedom = chunks[j].getSingleWordsMorphemicFreedom();
		}
		
		//get the items that the word length equals to
		//the max's length.
		ArrayList<IChunk> chunkArr = new ArrayList<IChunk>(chunks.length);
		for ( j = 0; j < chunks.length; j++ ) {
			if ( chunks[j].getSingleWordsMorphemicFreedom() == largestFreedom)
				chunkArr.add(chunks[j]);
		}
		
		IChunk[] lchunk = new IChunk[chunkArr.size()];
		chunkArr.toArray(lchunk);
		chunkArr.clear();
		
		return lchunk;
	}

}
