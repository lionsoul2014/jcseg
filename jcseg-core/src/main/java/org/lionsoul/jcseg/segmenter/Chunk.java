package org.lionsoul.jcseg.segmenter;

import org.lionsoul.jcseg.IChunk;
import org.lionsoul.jcseg.IWord;

/**
 * <p>chunk concept for the mmseg word segmentation algorithm</p>
 * 
 * @author	chenxin<chenxin619315@gmail.com>
 */
public class Chunk implements IChunk
{
    /**
     * the word array 
     */
    private final IWord[] words;
    
    /**
     * the average words length 
     */
    private double avgWordsLength = -1D;
    
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
    
    
    public Chunk( IWord[] words )
    {
        this.words = words;
    }

    /**
     * @see IChunk#getWords() 
     */
    @Override
    public IWord[] getWords()
    {
        return words;
    }

    /**
     * @see IChunk#getAverageWordsLength() 
     */
    @Override
    public double getAverageWordsLength()
    {
        if ( avgWordsLength == -1D ) {
        	avgWordsLength = (double) getLength() / (double) words.length;
        }
        
        return avgWordsLength;
    }

    /**
     * @see IChunk#getWordsVariance() 
     */
    @Override
    public double getWordsVariance()
    {
        if ( wordsVariance == -1D ) {
            double variance = 0D, temp;
            for (IWord word : words) {
                temp = (double) word.getLength() - getAverageWordsLength();
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
    public double getSingleWordsMorphemicFreedom()
    {
        if ( singleWordMorphemicFreedom == -1D ) {
        	singleWordMorphemicFreedom = 0;
            for (IWord word : words) {
                //one-character word
                if (word.getLength() == 1) {
                    singleWordMorphemicFreedom += Math.log((double) word.getFrequency());
                }
            }
        } 
        
        return singleWordMorphemicFreedom;
    }

    /**
     * @see IChunk#getLength() 
     */
    @Override
    public int getLength()
    {
        if ( length == -1 ) {
        	length = 0;
            for (IWord word : words) {
                length += word.getValue().length();
            }
        } 
        
        return length;
    }
    
    /**
     * @see Object#toString() 
     */
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append("chunk: ");
        for (IWord word : words) {
            sb.append(word).append("/");
        }
        
        return sb.toString();
    }
}
