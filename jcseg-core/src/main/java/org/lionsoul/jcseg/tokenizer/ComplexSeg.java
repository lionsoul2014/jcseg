package org.lionsoul.jcseg.tokenizer;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//import java.util.Iterator;



import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.IChunk;
import org.lionsoul.jcseg.tokenizer.core.ILexicon;
import org.lionsoul.jcseg.tokenizer.core.IWord;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;
import org.lionsoul.jcseg.tokenizer.core.MMSegFilter;


/**
 * <p>
 * Jcseg complex segmentation implements extended from the ASegment class 
 * this will need the filter works of the four MMSeg rules: 
 * </p>
 * 
 * <ul>
 * <li>1.maximum match chunk.</li>
 * <li>2.largest average word length.</li>
 * <li>3.smallest variance of words length.</li>
 * <li>4.largest sum of degree of morphemic freedom of one-character words.</li>
 * </ul>
 * 
 * @author  chenxin<chenxin619315@gmail.com>
*/
public class ComplexSeg extends Segment implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public ComplexSeg( JcsegTaskConfig config, ADictionary dic ) throws IOException 
    {
        super(config, dic);
    }
    
    public ComplexSeg( Reader input, JcsegTaskConfig config, ADictionary dic ) throws IOException 
    {
        super(input, config, dic);
    }

    /**
     * @see Segment#getBestChunk(char[], int, int)
     */
    @Override
    public IChunk getBestChunk(char chars[], int index, int maxLen)
    {
    	/* create a global word list buffer */
    	final List<IWord> wList = new ArrayList<IWord>(12);
        IWord[] mwords = getNextMatch(maxLen, chars, index, wList), mword2, mword3;
        if ( mwords.length == 1 
                && mwords[0].getType() == ILexicon.UNMATCH_CJK_WORD ) {
            return new Chunk(new IWord[]{mwords[0]});
        }
        
        int idx_2, idx_3;
        ArrayList<IChunk> chunkArr = new ArrayList<IChunk>();
        for ( int x = 0; x < mwords.length; x++ ) {
            //the second layer
            idx_2 = index + mwords[x].getLength();
            if ( idx_2 < chars.length ) {
                mword2 = getNextMatch(maxLen, chars, idx_2, wList);
                /*
                 * the first try for the second layer
                 * returned a UNMATCH_CJK_WORD
                 * here, just return the largest length word in
                 * the first layer. 
                 */
                if ( mword2.length == 1
                        && mword2[0].getType() == ILexicon.UNMATCH_CJK_WORD) {
                    return new Chunk(new IWord[]{mwords[mwords.length - 1]});
                }
                
                for ( int y = 0; y < mword2.length; y++ ) {
                    //the third layer
                    idx_3 = idx_2 + mword2[y].getLength();
                    if ( idx_3 < chars.length ) {
                        mword3 = getNextMatch(maxLen, chars, idx_3, wList);
                        for ( int z = 0; z < mword3.length; z++ ) {
                            ArrayList<IWord> wArr = new ArrayList<IWord>(3);
                            wArr.add(mwords[x]);
                            wArr.add(mword2[y]);
                            if ( mword3[z].getType() != ILexicon.UNMATCH_CJK_WORD )
                                wArr.add(mword3[z]);
                            
                            IWord[] words = new IWord[wArr.size()];
                            wArr.toArray(words);
                            wArr.clear();
                            
                            chunkArr.add(new Chunk(words));
                        }
                    } else {
                        chunkArr.add(new Chunk(new IWord[]{mwords[x], mword2[y]}));
                    }
                }
            } else {
                chunkArr.add(new Chunk(new IWord[]{mwords[x]}));
            }
        }
        
        wList.clear();
        if ( chunkArr.size() == 1 ) {
            return chunkArr.get(0);
        }
        
/*        Iterator<IChunk> it = chunkArr.iterator();
        while ( it.hasNext() ) {
            System.out.println(it.next());
        }
        System.out.println("-+---------------------+-");*/
        
        IChunk[] chunks = new IChunk[chunkArr.size()];
        chunkArr.toArray(chunks);
        chunkArr.clear();
        
        mwords = null;
        mword2 = null;
        mword3 = null;
        
        
        //-------------------------MMSeg core invoke------------------------
        
        //filter the maximum match rule.
        IChunk[] afterChunks = MMSegFilter.getMaximumMatchChunks(chunks);
        if ( afterChunks.length == 1 ) {
            return afterChunks[0];
        }
        
        //filter the largest average rule.
        afterChunks = MMSegFilter.getLargestAverageWordLengthChunks(afterChunks);
        if ( afterChunks.length == 1 ) {
            return afterChunks[0];
        }
        
        //filter the smallest variance rule.
        afterChunks = MMSegFilter.getSmallestVarianceWordLengthChunks(afterChunks);
        if ( afterChunks.length == 1 ) {
            return afterChunks[0];
        }
        
        //filter the largest sum of degree of morphemic freedom rule.
        afterChunks = MMSegFilter.getLargestSingleMorphemicFreedomChunks(afterChunks);
        if ( afterChunks.length == 1 ) {
            return afterChunks[0];
        }
        
        //consider this as the final rule
        //Change it to return the last chunk at 2017/07/04
        //return afterChunks[0];
        return afterChunks[afterChunks.length - 1];
    }
    
}
