package org.lionsoul.jcseg.tokenizer;

import org.lionsoul.jcseg.Chunk;
import org.lionsoul.jcseg.IChunk;
import org.lionsoul.jcseg.IWord;
import org.lionsoul.jcseg.JcsegTaskConfig;
import org.lionsoul.jcseg.dic.ADictionary;


/**
 * Jcseg simple segmentation implementation
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class SimpleSeg extends Segment
{
    
    public SimpleSeg( JcsegTaskConfig config, ADictionary dic )
    {
        super(config, dic);
    }

    /**
     * @see Segment#getBestChunk(char[], int, int)
     */
    @Override
    public IChunk getBestChunk(char[] chars, int index, int maxLen)
    {
        IWord[] words = getNextMatch(maxLen, chars, index, null);
        return new Chunk(new IWord[]{words[words.length - 1]});
    }
    
}
