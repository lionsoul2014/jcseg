package org.lionsoul.jcseg.segmenter;

import org.lionsoul.jcseg.IChunk;
import org.lionsoul.jcseg.IWord;
import org.lionsoul.jcseg.dic.ADictionary;


/**
 * Jcseg simple segmentation implementation
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class SimpleSeg extends Segmenter
{
    
    public SimpleSeg( SegmenterConfig config, ADictionary dic )
    {
        super(config, dic);
    }

    /**
     * @see Segmenter#getBestChunk(char[], int, int)
     */
    @Override
    public IChunk getBestChunk(char[] chars, int index, int maxLen)
    {
        final IWord[] words = getNextMatch(maxLen, chars, index, null);
        return new Chunk(new IWord[]{words[words.length - 1]});
    }
    
}
