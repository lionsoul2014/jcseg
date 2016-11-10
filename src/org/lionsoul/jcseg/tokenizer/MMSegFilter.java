package org.lionsoul.jcseg.tokenizer;

import java.util.ArrayList;

import org.lionsoul.jcseg.tokenizer.core.IChunk;

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
    public static IChunk[] getMaximumMatchChunks(IChunk[] chunks) 
    {
        int maxLength = chunks[0].getLength();
        int j;
        //find the maximum word length
        for ( j = 1; j < chunks.length; j++ ) {
            if ( chunks[j].getLength() > maxLength ) 
                maxLength = chunks[j].getLength();
        }
        
        //get the items that the word length equals to
        //the max's length.
        ArrayList<IChunk> chunkArr = new ArrayList<IChunk>(chunks.length);
        for ( j = 0; j < chunks.length; j++ ) {
            if ( chunks[j].getLength() == maxLength) {
                chunkArr.add(chunks[j]);
            }
        }
        
        IChunk[] lchunk = new IChunk[chunkArr.size()];
        chunkArr.toArray(lchunk);
        chunkArr.clear();
        
        return lchunk;
    }
    
    
    /**
     * 2. largest average word length
     * this rule will return the chunks that own the largest average word length
    */
    public static IChunk[] getLargestAverageWordLengthChunks(IChunk[] chunks) 
    {
        double largetAverage = chunks[0].getAverageWordsLength();
        int j;
        
        //find the largest average word length
        for ( j = 1; j < chunks.length; j++ ) {
            if ( chunks[j].getAverageWordsLength() > largetAverage ) {
                largetAverage = chunks[j].getAverageWordsLength();
            }
        }
        
        //get the items that the average word length equals to
        //the max's.
        ArrayList<IChunk> chunkArr = new ArrayList<IChunk>(chunks.length);
        for ( j = 0; j < chunks.length; j++ ) {
            if ( chunks[j].getAverageWordsLength() == largetAverage) {
                chunkArr.add(chunks[j]);
            }
        }
        
        IChunk[] lchunk = new IChunk[chunkArr.size()];
        chunkArr.toArray(lchunk);
        chunkArr.clear();
        
        return lchunk;
    }
    
    /**
     * the smallest variance word length
     * this rule will the chunks that one the smallest variance word length
    */
    public static IChunk[] getSmallestVarianceWordLengthChunks(IChunk[] chunks) 
    {
        double smallestVariance = chunks[0].getWordsVariance();
        int j;
        
        //find the smallest variance word length
        for ( j = 1; j < chunks.length; j++ ) {
            if ( chunks[j].getWordsVariance() < smallestVariance ) {
                smallestVariance = chunks[j].getWordsVariance();
            }
        }
        
        //get the items that the variance word length equals to
        //the max's.
        ArrayList<IChunk> chunkArr = new ArrayList<IChunk>(chunks.length);
        for ( j = 0; j < chunks.length; j++ ) {
            if ( chunks[j].getWordsVariance() == smallestVariance) {
                chunkArr.add(chunks[j]);
            }
        }
        
        IChunk[] lchunk = new IChunk[chunkArr.size()];
        chunkArr.toArray(lchunk);
        chunkArr.clear();
        
        return lchunk;
    }
    
    
    /**
     * the largest sum of degree of morphemic freedom of one-character words
     * this rule will return the chunks that own the largest sum of degree of morphemic freedom 
     * of one-character
    */
    public static IChunk[] getLargestSingleMorphemicFreedomChunks(IChunk[] chunks) 
    {
        double largestFreedom = chunks[0].getSingleWordsMorphemicFreedom();
        int j;
        
        //find the maximum sum of single morphemic freedom
        for ( j = 1; j < chunks.length; j++ ) {
            if ( chunks[j].getSingleWordsMorphemicFreedom() > largestFreedom ) {
                largestFreedom = chunks[j].getSingleWordsMorphemicFreedom();
            }
        }
        
        //get the items that the word length equals to
        //the max's length.
        ArrayList<IChunk> chunkArr = new ArrayList<IChunk>(chunks.length);
        for ( j = 0; j < chunks.length; j++ ) {
            if ( chunks[j].getSingleWordsMorphemicFreedom() == largestFreedom) {
                chunkArr.add(chunks[j]);
            }
        }
        
        IChunk[] lchunk = new IChunk[chunkArr.size()];
        chunkArr.toArray(lchunk);
        chunkArr.clear();
        
        return lchunk;
    }
}