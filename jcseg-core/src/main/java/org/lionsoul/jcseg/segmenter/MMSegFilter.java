package org.lionsoul.jcseg.segmenter;

import java.util.ArrayList;

import org.lionsoul.jcseg.IChunk;

/**
 * MMSeg default filter class
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class MMSegFilter 
{
    /**
     * 1. the maximum match rule
     * this rule will return the chunks that own the largest word length
    */
    public static ArrayList<IChunk> getMaximumMatchChunks(ArrayList<IChunk> inChunks, ArrayList<IChunk> outChunks)
    {
        int maxLength = inChunks.get(0).getLength();
        int j;
        //find the maximum word length
        for ( j = 1; j < inChunks.size(); j++ ) {
            final int l = inChunks.get(j).getLength();
            if (l > maxLength) {
                maxLength = l;
            }
        }
        
        //get the items that the word length equals to the largest.
        outChunks.clear();
        for (final IChunk c : inChunks) {
            if (c.getLength() == maxLength) {
                outChunks.add(c);
            }
        }

        return outChunks;
    }
    
    
    /**
     * 2. largest average word length
     * this rule will return the chunks that own the largest average word length
    */
    public static ArrayList<IChunk> getLargestAverageWordLengthChunks(ArrayList<IChunk> inChunks, ArrayList<IChunk> outChunks)
    {
        double largestAverage = inChunks.get(0).getAverageWordsLength();
        int j;
        
        //find the largest average word length
        for ( j = 1; j < inChunks.size(); j++ ) {
            final double avg = inChunks.get(j).getAverageWordsLength();
            if (avg > largestAverage) {
                largestAverage = avg;
            }
        }
        
        //get the items that the average word length equals to the largest.
        outChunks.clear();
        for (final IChunk c : inChunks) {
            if (c.getAverageWordsLength() == largestAverage) {
                outChunks.add(c);
            }
        }
        
        return outChunks;
    }
    
    /**
     * the smallest variance word length
     * this rule will the chunks that one the smallest variance word length
    */
    public static ArrayList<IChunk> getSmallestVarianceWordLengthChunks(ArrayList<IChunk> inChunks, ArrayList<IChunk> outChunks)
    {
        double smallestVariance = inChunks.get(0).getWordsVariance();
        int j;
        
        //find the smallest variance word length
        for ( j = 1; j < inChunks.size(); j++ ) {
            final double v = inChunks.get(j).getWordsVariance();
            if (v < smallestVariance ) {
                smallestVariance = v;
            }
        }
        
        //get the items that the variance word length equals to the largest
        outChunks.clear();
        for (final IChunk c : inChunks) {
            if (c.getWordsVariance() == smallestVariance) {
                outChunks.add(c);
            }
        }
        
        return outChunks;
    }
    
    
    /**
     * the largest sum of degree of morphemic freedom of one-character words
     * this rule will return the chunks that own the largest sum of degree of morphemic freedom 
     * of one-character
    */
    public static ArrayList<IChunk> getLargestSingleMorphemicFreedomChunks(ArrayList<IChunk> inChunks, ArrayList<IChunk> outChunks)
    {
        double largestFreedom = inChunks.get(0).getSingleWordsMorphemicFreedom();
        int j;
        
        //find the maximum sum of single morphemic freedom
        for (j = 1; j < inChunks.size(); j++ ) {
            final double f = inChunks.get(j).getSingleWordsMorphemicFreedom();
            if (f > largestFreedom) {
                largestFreedom = f;
            }
        }


        //get the items that the word length equals to the largest.
        outChunks.clear();
        for (final IChunk c : inChunks) {
            if (c.getSingleWordsMorphemicFreedom() == largestFreedom) {
                outChunks.add(c);
            }
        }
        
        return outChunks;
    }
    
}