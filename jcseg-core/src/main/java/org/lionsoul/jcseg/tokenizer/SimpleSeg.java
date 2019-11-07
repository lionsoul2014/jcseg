package org.lionsoul.jcseg.tokenizer;

import java.io.IOException;
import java.io.Reader;

import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.IChunk;
import org.lionsoul.jcseg.tokenizer.core.IWord;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;


/**
 * Jcseg simple segmentation implements extend from ASegment
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class SimpleSeg extends Segment
{
    
    public SimpleSeg( JcsegTaskConfig config, ADictionary dic ) throws IOException 
    {
        super(config, dic);
    }
    
    public SimpleSeg( Reader input, JcsegTaskConfig config, ADictionary dic ) throws IOException 
    {
        super(input, config, dic);
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
