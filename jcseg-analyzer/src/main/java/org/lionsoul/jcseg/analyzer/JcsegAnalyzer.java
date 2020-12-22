package org.lionsoul.jcseg.analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.lionsoul.jcseg.ISegment;
import org.lionsoul.jcseg.dic.ADictionary;
import org.lionsoul.jcseg.segmenter.SegmenterConfig;

/**
 * Jcseg analyzer for lucene with version on or after 5.0
 * 
 * @author    chenxin<chenxin619315@gmail.com>
 */
public final class JcsegAnalyzer extends Analyzer
{
    public ISegment.Type type;
    public final SegmenterConfig config;
    public final ADictionary dic;
    
    /**
     * initialize the analyzer with the specified mode, configuration, dictionary
     * 
     * @param   type
     * @param   config
     * @param   dic
    */
    public JcsegAnalyzer(ISegment.Type type, SegmenterConfig config, ADictionary dic)
    {
        this.type   = type;
        this.config = config;
        this.dic    = dic;
    }
    
    public SegmenterConfig getConfig()
    {
        return config;
    }
    
    public ADictionary getDict()
    {
        return dic;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) 
    {
        final Tokenizer tokenizer = new JcsegTokenizer(type, config, dic);
        return new TokenStreamComponents(tokenizer);
    }
}
