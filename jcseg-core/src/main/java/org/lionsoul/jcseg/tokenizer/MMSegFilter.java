package org.lionsoul.jcseg.tokenizer;

import java.util.ArrayList;

import org.lionsoul.jcseg.IChunk;

/**
 * mmseg default filter class
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class MMSegFilter 
{
    /**
     * 1. the maximum match rule
     * this rule will return the chunks that own the largest word length
    */
    public static ArrayList<IChunk> getMaximumMatchChunks(ArrayList<IChunk> chunks, ArrayList<IChunk> chunkArr) 
    {
        int maxLength = chunks.get(0).getLength();
        int j;
        //find the maximum word length
        for ( j = 1; j < chunks.size(); j++ ) {
            if ( chunks.get(j).getLength() > maxLength ) {
                maxLength = chunks.get(j).getLength();
            }
        }
        
        //get the items that the word length equals to the largest.
        chunkArr.clear();
        for ( j = 0; j < chunks.size(); j++ ) {
            if ( chunks.get(j).getLength() == maxLength) {
                chunkArr.add(chunks.get(j));
            }
        }
        
        return chunkArr;
    }
    
    
    /**
     * 2. largest average word length
     * this rule will return the chunks that own the largest average word length
    */
    public static ArrayList<IChunk> getLargestAverageWordLengthChunks(ArrayList<IChunk> chunks, ArrayList<IChunk> chunkArr) 
    {
        double largetAverage = chunks.get(0).getAverageWordsLength();
        int j;
        
        //find the largest average word length
        for ( j = 1; j < chunks.size(); j++ ) {
            if ( chunks.get(j).getAverageWordsLength() > largetAverage ) {
                largetAverage = chunks.get(j).getAverageWordsLength();
            }
        }
        
        //get the items that the average word length equals to the largest.
        chunkArr.clear();
        for ( j = 0; j < chunks.size(); j++ ) {
            if ( chunks.get(j).getAverageWordsLength() == largetAverage) {
                chunkArr.add(chunks.get(j));
            }
        }
        
        return chunkArr;
    }
    
    /**
     * 2 
    */
    
    /**
     * the smallest variance word length
     * this rule will the chunks that one the smallest variance word length
    */
    public static ArrayList<IChunk> getSmallestVarianceWordLengthChunks(ArrayList<IChunk> chunks, ArrayList<IChunk> chunkArr) 
    {
        double smallestVariance = chunks.get(0).getWordsVariance();
        int j;
        
        //find the smallest variance word length
        for ( j = 1; j < chunks.size(); j++ ) {
            if ( chunks.get(j).getWordsVariance() < smallestVariance ) {
                smallestVariance = chunks.get(j).getWordsVariance();
            }
        }
        
        //get the items that the variance word length equals to the largest
        chunkArr.clear();
        for ( j = 0; j < chunks.size(); j++ ) {
            if ( chunks.get(j).getWordsVariance() == smallestVariance) {
                chunkArr.add(chunks.get(j));
            }
        }
        
        return chunkArr;
    }
    
    
    /**
     * the largest sum of degree of morphemic freedom of one-character words
     * this rule will return the chunks that own the largest sum of degree of morphemic freedom 
     * of one-character
    */
    public static ArrayList<IChunk> getLargestSingleMorphemicFreedomChunks(ArrayList<IChunk> chunks, ArrayList<IChunk> chunkArr) 
    {
        double largestFreedom = chunks.get(0).getSingleWordsMorphemicFreedom();
        int j;
        
        //find the maximum sum of single morphemic freedom
        for ( j = 1; j < chunks.size(); j++ ) {
            if ( chunks.get(j).getSingleWordsMorphemicFreedom() > largestFreedom ) {
                largestFreedom = chunks.get(j).getSingleWordsMorphemicFreedom();
            }
        }
        
        //get the items that the word length equals to the largest.
        chunkArr.clear();
        for ( j = 0; j < chunks.size(); j++ ) {
            if ( chunks.get(j).getSingleWordsMorphemicFreedom() == largestFreedom) {
                chunkArr.add(chunks.get(j));
            }
        }
        
        return chunkArr;
    }
    
}