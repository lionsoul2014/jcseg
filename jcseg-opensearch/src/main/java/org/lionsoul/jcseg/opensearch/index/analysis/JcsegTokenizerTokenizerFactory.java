package org.lionsoul.jcseg.opensearch.index.analysis;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.lucene.analysis.Tokenizer;
import org.lionsoul.jcseg.ISegment;
import org.lionsoul.jcseg.analyzer.JcsegTokenizer;
import org.lionsoul.jcseg.dic.ADictionary;
import org.lionsoul.jcseg.opensearch.plugin.AnalysisJcsegPlugin;
import org.lionsoul.jcseg.segmenter.SegmenterConfig;
import org.opensearch.common.settings.Settings;
import org.opensearch.env.Environment;
import org.opensearch.index.IndexSettings;
import org.opensearch.index.analysis.AbstractTokenizerFactory;

public class JcsegTokenizerTokenizerFactory extends AbstractTokenizerFactory
{
    private final SegmenterConfig config;
    private final ADictionary dic;
    private final ISegment.Type type;

    public JcsegTokenizerTokenizerFactory(
            IndexSettings indexSettings, Environment environment, String name, Settings settings) throws IOException {
        super(indexSettings, settings, name);
        
        config = new SegmenterConfig(
                Files.newInputStream(AnalysisJcsegPlugin.getPluginSafeFile("jcseg.properties").toPath()));
        
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
