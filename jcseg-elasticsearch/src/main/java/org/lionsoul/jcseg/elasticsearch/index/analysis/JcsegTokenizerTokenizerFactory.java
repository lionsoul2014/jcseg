package org.lionsoul.jcseg.elasticsearch.index.analysis;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;
import org.lionsoul.jcseg.ISegment;
import org.lionsoul.jcseg.analyzer.JcsegTokenizer;
import org.lionsoul.jcseg.dic.ADictionary;
import org.lionsoul.jcseg.elasticsearch.plugin.AnalysisJcsegPlugin;
import org.lionsoul.jcseg.segmenter.SegmenterConfig;

public class JcsegTokenizerTokenizerFactory extends AbstractTokenizerFactory
{
    private final SegmenterConfig config;
    private final ADictionary dic;
    private final ISegment.Type type;

    public JcsegTokenizerTokenizerFactory(
            IndexSettings indexSettings, Environment environment, String name, Settings settings) throws IOException {
        super(indexSettings, settings, name);
        
        config = new SegmenterConfig(Files.newInputStream(AnalysisJcsegPlugin.getPluginSafeFile("jcseg.properties").toPath()));
        
        /* Load and apply the self-define arguments for Jcseg */
        for ( String key : settings.names() ) {
        	if ( key.startsWith("jcseg_") ) {
        		config.set(key.replace("jcseg_", "jcseg."), settings.get(key));
        	}
        }
        
        dic  = AnalysisJcsegPlugin.createSingletonDictionary(config);
        type = ISegment.Type.fromString(settings.get("seg_mode"));
    }

    @Override
    public Tokenizer create() 
    {
    	return new JcsegTokenizer(type, config, dic);
    }
    
}
