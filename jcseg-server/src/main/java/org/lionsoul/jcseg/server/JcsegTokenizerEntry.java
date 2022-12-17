package org.lionsoul.jcseg.server;

import org.lionsoul.jcseg.dic.ADictionary;
import org.lionsoul.jcseg.segmenter.SegmenterConfig;

/**
 *  TokenizerEntry to save setting for Tokenizer object 
 * 
 * @author dongyado<dongyado@gmail.com>
 * @author chenxin<chenxin619315@gmail.com>
*/
public class JcsegTokenizerEntry {
    
    private final int algorithm;
    private final SegmenterConfig config;
    private final ADictionary dic;
    
    public JcsegTokenizerEntry(int algorithm, SegmenterConfig config, ADictionary dic )
    {
        this.algorithm = algorithm;
        this.config = config;
        this.dic = dic;
    }
    
    public SegmenterConfig getConfig()
    {
        return config;
    }

    public ADictionary getDict()
    {
        return dic;
    }

    public int getAlgorithm()
    {
        return algorithm;
    }

}
