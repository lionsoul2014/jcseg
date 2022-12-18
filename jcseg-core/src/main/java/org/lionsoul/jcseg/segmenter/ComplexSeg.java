package org.lionsoul.jcseg.segmenter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.lionsoul.jcseg.IChunk;
import org.lionsoul.jcseg.IWord;
import org.lionsoul.jcseg.dic.ADictionary;
import org.lionsoul.jcseg.dic.ILexicon;


/**
 * <p>
 * Jcseg complex segmentation implementation based on the filter works of MMSeg rules: 
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
public class ComplexSeg extends Segmenter implements Serializable
{
	
    private static final long serialVersionUID = 1L;
    
    public ComplexSeg( SegmenterConfig config, ADictionary dic )
    {
        super(config, dic);
    }

    /**
     * @see Segmenter#getBestChunk(char[], int, int)
     */
    @Override
    public IChunk getBestChunk(char[] chars, int index, int maxLen)
    {
    	/* create a global word list buffer */
    	final List<IWord> wList = new ArrayList<IWord>(12);
        IWord[] mWords = getNextMatch(maxLen, chars, index, wList), mword2, mword3;
        if ( mWords.length == 1
                && mWords[0].getType() == ILexicon.UNMATCH_CJK_WORD ) {
            return new Chunk(new IWord[]{mWords[0]});
        }
        
        int idx_2, idx_3;
        final ArrayList<IChunk> chunkArr = new ArrayList<IChunk>();
        for (int x = 0; x < mWords.length; x++ ) {
            //the second layer
            idx_2 = index + mWords[x].getLength();
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
                    return new Chunk(new IWord[]{mWords[mWords.length - 1]});
                }

                for ( int y = 0; y < mword2.length; y++ ) {
                    //the third layer
                    idx_3 = idx_2 + mword2[y].getLength();
                    if ( idx_3 < chars.length ) {
                        mword3 = getNextMatch(maxLen, chars, idx_3, wList);
                        for ( int z = 0; z < mword3.length; z++ ) {
                            final ArrayList<IWord> wArr = new ArrayList<>(3);
                            wArr.add(mWords[x]);
                            wArr.add(mword2[y]);
                            if ( mword3[z].getType() != ILexicon.UNMATCH_CJK_WORD )
                                wArr.add(mword3[z]);

                            final IWord[] words = new IWord[wArr.size()];
                            wArr.toArray(words);
                            wArr.clear();

                            chunkArr.add(new Chunk(words));
                        }
                    } else {
                        chunkArr.add(new Chunk(new IWord[]{mWords[x], mword2[y]}));
                    }
                }
            } else {
                chunkArr.add(new Chunk(new IWord[]{mWords[x]}));
            }
        }
        
        wList.clear();
        if ( chunkArr.size() == 1 ) {
            return chunkArr.get(0);
        }

        mWords = null;
        mword2 = null;
        mword3 = null;
        /// printChunks("#0=>Init: ", chunkArr);

        
        //-------------------------MMSeg core invoke------------------------
        final ArrayList<IChunk> chunkBuf = new ArrayList<>(chunkArr.size());
        
        //filter the maximum match rule.
        ArrayList<IChunk> chunks = MMSegFilter.getMaximumMatchChunks(chunkArr, chunkBuf);
        /// printChunks("#1=>MaximumMatchChunks: ", chunks);
        if ( chunks.size() == 1 ) {
            return chunks.get(0);
        }

        //filter the largest average rule.
        chunks = MMSegFilter.getLargestAverageWordLengthChunks(chunkBuf, chunkArr);
        /// printChunks("#2=>LargestAverageWordLengthChunks: ", chunks);
        if ( chunks.size() == 1 ) {
            return chunks.get(0);
        }

        //filter the smallest variance rule.
        chunks = MMSegFilter.getSmallestVarianceWordLengthChunks(chunkArr, chunkBuf);
        // printChunks("3#=>SmallestVarianceWordLengthChunks: ", chunks);
        if ( chunks.size() == 1 ) {
            return chunks.get(0);
        }

        //filter the largest sum of degree of morphemic freedom rule.
        chunks = MMSegFilter.getLargestSingleMorphemicFreedomChunks(chunkBuf, chunkArr);
        /// printChunks("4#=>LargestSingleMorphemicFreedomChunks: ", chunks);
        if ( chunks.size() == 1 ) {
            return chunks.get(0);
        }

        //consider this as the final rule
        //Change it to return the last chunk at 2017/07/04
        //return afterChunks[0];
        return chunks.get(chunks.size() - 1);
    }

    // for debug
    protected static void printChunks(final String scene, final ArrayList<IChunk> chunks)
    {
        System.out.println(scene);
        for (IChunk c : chunks) {
            System.out.println(c.toString());
        }
        System.out.println("-+------------------------+-");
    }

}
