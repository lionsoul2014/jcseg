package org.lionsoul.jcseg.elasticsearch.index.analysis;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;
import org.lionsoul.jcseg.analyzer.JcsegTokenizer;
import org.lionsoul.jcseg.elasticsearch.plugin.AnalysisJcsegPlugin;
import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.JcsegException;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;

public class JcsegTokenizerTokenizerFactory extends AbstractTokenizerFactory
{
    private final JcsegTaskConfig config;
    private final ADictionary dic;
    private final int mode;

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
        
        dic = AnalysisJcsegPlugin.createSingletonDictionary(config);

        String seg_mode = settings.get("seg_mode");
        if( "complex".equals(seg_mode) ) {
            mode = JcsegTaskConfig.COMPLEX_MODE;
        } else if ( "simple".equals(seg_mode) ) {
            mode = JcsegTaskConfig.SIMPLE_MODE;
        } else if ( "detect".equals(seg_mode) ) {
            mode = JcsegTaskConfig.DETECT_MODE;
        } else if ( "search".equals(seg_mode) ) {
            mode = JcsegTaskConfig.SEARCH_MODE;
        } else if ( "nlp".equals(seg_mode) ){
            mode = JcsegTaskConfig.NLP_MODE;
        } else if ( "delimiter".equals(seg_mode) ) {
            mode = JcsegTaskConfig.DELIMITER_MODE;
        } else {
            mode = JcsegTaskConfig.SEARCH_MODE;
        }
    }

    @Override
    public Tokenizer create() 
    {
        try {
            return new JcsegTokenizer(mode, config, dic);
        } catch (JcsegException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
