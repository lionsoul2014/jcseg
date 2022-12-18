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
     * single word degree of morphemic freedom.
     */
    private double singleWordMorphemicFreedom = -1;

    /*
     * single word frequency
     */
    private int singleWordFrequency = -1;

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
                    singleWordMorphemicFreedom += word.getFrequency() > 0 ? Math.log(word.getFrequency()) : 0;
                }
            }
        } 
        
        return singleWordMorphemicFreedom;
    }

    @Override
    public int getSingleWordsFrequency()
    {
        if ( singleWordFrequency == -1D ) {
            singleWordFrequency = 0;
            for (IWord word : words) {
                //one-character word
                if (word.getLength() == 1) {
                    singleWordFrequency += Math.max(word.getFrequency(), 0);
                }
            }
        }

        return singleWordFrequency;
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
        final String prefix = "chunk: ";
        sb.append(prefix);
        for (IWord word : words) {
            if (sb.length() > prefix.length()) {
               sb.append("/") ;
            }
            sb.append(word);
        }
        
        return sb.toString();
    }

}
