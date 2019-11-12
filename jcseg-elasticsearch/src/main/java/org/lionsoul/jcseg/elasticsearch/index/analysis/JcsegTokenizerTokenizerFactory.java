package org.lionsoul.jcseg.elasticsearch.index.analysis;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;
import org.lionsoul.jcseg.ISegment;
import org.lionsoul.jcseg.JcsegTaskConfig;
import org.lionsoul.jcseg.analyzer.JcsegTokenizer;
import org.lionsoul.jcseg.dic.ADictionary;
import org.lionsoul.jcseg.elasticsearch.plugin.AnalysisJcsegPlugin;

public class JcsegTokenizerTokenizerFactory extends AbstractTokenizerFactory
{
    private final JcsegTaskConfig config;
    private final ADictionary dic;
    private final ISegment.Type type;

    public JcsegTokenizerTokenizerFactory(
            IndexSettings indexSettings, Environment environment, String name, Settings settings) throws IOException {
        super(indexSettings, settings, name);
        
        config = new JcsegTaskConfig(new FileInputStream(AnalysisJcsegPlugin.getPluginSafeFile("jcseg.properties")));
        
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
        try {
            return new JcsegTokenizer(type, config, dic);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    
}
