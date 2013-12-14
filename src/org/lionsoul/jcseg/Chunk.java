package org.lionsoul.jcseg;

import org.lionsoul.jcseg.core.IChunk;
import org.lionsoul.jcseg.core.IWord;

/**
 * chunk concept for the mmseg chinese word segment algorithm.
 * 		has implemented IChunk interface.
 * 
 * @author	chenxin<chenxin619315@gmail.com>
 */
public class Chunk implements IChunk {
	
	/**
	 * the word array 
	 */
	private IWord[] words;
	
	/**
	 * the average words length 
	 */
	private double averageWordsLength = -1D;
	
	/**
	 * the words variance 
	 */
	private double wordsVariance = -1D;
	
	/**
	 * single word degree of morphemic freedom 
	 */
	private double singleWordMorphemicFreedom = -1D;
	
	/**
	 * words length 
	 */
	private int length = -1;
	
	
	public Chunk( IWord[] words ) {
		this.words = words;
	}

	/**
	 * @see IChunk#getWords() 
	 */
	@Override
	public IWord[] getWords() {
		return words;
	}

	/**
	 * @see IChunk#getAverageWordsLength() 
	 */
	@Override
	public double getAverageWordsLength() {
		if ( averageWordsLength == -1D ) {
			averageWordsLength = (double) getLength() / (double) words.length;
		}
		return averageWordsLength;
	}

	/**
	 * @see IChunk#getWordsVariance() 
	 */
	@Override
	public double getWordsVariance() {
		if ( wordsVariance == -1D ) {
			double variance = 0D, temp;
			for ( int j = 0; j < words.length; j++ ) {
				temp = (double) words[j].getLength() - getAverageWordsLength();
				variance = variance + temp * temp;
			}
			//wordsVariance = Math.sqrt( variance / (double) words.length );
			wordsVariance = variance / words.length;
		}
		return wordsVariance;
	}

	/**
	 * @see IChunk#getSingleWordsMorphemicFreedom()
	 */
	@Override
	public double getSingleWordsMorphemicFreedom() {
		if ( singleWordMorphemicFreedom == -1D ) {
			singleWordMorphemicFreedom = 0;
			for ( int j = 0; j < words.length; j++ ) {
				//one-character word
				if ( words[j].getLength() == 1 ) {
					singleWordMorphemicFreedom = singleWordMorphemicFreedom 
						//+ words[j].getFrequency();
						+ Math.log((double) words[j].getFrequency());
				}
			}
		} 
		return singleWordMorphemicFreedom;
	}

	/**
	 * @see IChunk#getLength() 
	 */
	@Override
	public int getLength() {
		if ( length == -1 ) {
			length = 0;
			for ( int j = 0; j < words.length; j++ ) {
				length = length + words[j].getLength();
			}
		} 
		return length;
	}
	
	/**
	 * @see Object#toString() 
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("chunk: ");
		for ( int j = 0; j < words.length; j++ ) {
			sb.append(words[j]+"/");
		}
		return sb.toString();
	}

}
