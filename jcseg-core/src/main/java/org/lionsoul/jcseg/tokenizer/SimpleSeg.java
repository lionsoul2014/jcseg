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
public class SimpleSeg extends ASegment
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
     * @see ASegment#getBestCJKChunk(char[], int) 
     */
    @Override
    public IChunk getBestCJKChunk(char[] chars, int index) throws IOException
    {
        IWord[] words = getNextMatch(chars, index);
        return new Chunk(new IWord[]{words[words.length - 1]});
    }
    
}
