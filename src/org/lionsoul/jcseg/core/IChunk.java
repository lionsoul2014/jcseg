package org.lionsoul.jcseg.core;

/**
 * chunk interface for JCSeg.
 * 		the most important concept for the mmseg chinese segment alogorithm.
 * 
 * @author	chenxin<chenxin619315@gmail.com>
 */
public interface IChunk {
	/**
	 * get the all the words in the chunk.
	 * 
	 * @return IWord[]
	 */
	public IWord[] getWords();
	
	/**
	 * return the average word length for all the chunks.
	 * 
	 * @return double
	 */
	public double getAverageWordsLength();
	
	/**
	 * return the variance of all the words in all
	 * 		the chunks.
	 * 
	 * @return double
	 */
	public double getWordsVariance();
	
	/**
	 * return the degree of morphemic freedom for all
	 * 		the single words.
	 * 
	 * @return double
	 */
	public double getSingleWordsMorphemicFreedom();
	
	/**
	 * return the length of the chunk(the number of the word)
	 * 
	 * @return int
	 */
	public int getLength();
}
