package org.lionsoul.jcseg.analyzer;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;
import org.lionsoul.jcseg.ISegment;
import org.lionsoul.jcseg.dic.ADictionary;
import org.lionsoul.jcseg.dic.DictionaryFactory;
import org.lionsoul.jcseg.segmenter.SegmenterConfig;

/**
 * Jcseg tokenizer factory class for lucene/solr
 * 
 * @author chenxin<chenxin619315@gmail.com>
 */
public class JcsegTokenizerFactory extends TokenizerFactory 
{
    
    public final ISegment.Type type;
    public final SegmenterConfig config;
    public final ADictionary dic;

    /**
     * set the mode arguments in the schema.xml 
     *     configuration file to change the segment mode for Jcseg
     *
     * @see TokenizerFactory#TokenizerFactory(Map)
     */
    public JcsegTokenizerFactory(Map<String, String> args) throws IOException
    {
        super(args);
        
        type = ISegment.Type.fromString(args.get("mode"));
        
        // initialize the task configuration and the dictionary
        config = new SegmenterConfig(true);
        // check and apply this-level Jcseg settings
        for ( Entry<String, String> entry : args.entrySet() ) {
        	if ( entry.getKey().startsWith("jcseg_") ) {
        		config.set(entry.getKey().replace("jcseg_", "jcseg."), entry.getValue());
        	}
        }
        
        dic = DictionaryFactory.createSingletonDictionary(config);
    }
    
    public SegmenterConfig getTaskConfig() 
    {
        return config;
    }
    
    public ADictionary getDict()
    {
        return dic;
    }

    @Override
    public Tokenizer create(AttributeFactory factory) 
    {
    	return new JcsegTokenizer(type, config, dic);
    }
}
