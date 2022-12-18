package org.lionsoul.jcseg;

/**
 * <p>
 * chunk interface for Jcseg.
 * The most important concept for the mmseg chinese segment algorithm
 * </p>
 * 
 * @author  chenxin<chenxin619315@gmail.com>
 */
public interface IChunk
{
    /** get the all the words in the chunk. */
    IWord[] getWords();
    
    /** return the average word length for all the chunks. */
    double getAverageWordsLength();
    
    /** return the variance of all the words in all the chunks. */
    double getWordsVariance();
    
    /** return the degree of morphemic freedom for all the single words. */
    double getSingleWordsMorphemicFreedom();

    int getSingleWordsFrequency();
    
    /** return the length of the chunk(the number of the word) */
    int getLength();
}
