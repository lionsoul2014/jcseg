package org.lionsoul.jcseg.elasticsearch.index.analysis;

import java.io.FileInputStream;
import java.io.IOException;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.lionsoul.jcseg.analyzer.JcsegAnalyzer;
import org.lionsoul.jcseg.elasticsearch.plugin.AnalysisJcsegPlugin;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;

/**
 * JcsegAnalyzerProvider
 * 
 * @author chenxin<chenxin619315@gmail.com>
 */
public abstract class JcsegAnalyzerProvider extends AbstractIndexAnalyzerProvider<JcsegAnalyzer>
{
    /**default Jcseg tokenizer instance*/
    private final JcsegAnalyzer analyzer;

    @Inject
    public JcsegAnalyzerProvider(
            IndexSettings indexSettings, Environment env, String name, Settings settings) throws IOException {
        super(indexSettings, name, settings);
        
        final JcsegTaskConfig config = new JcsegTaskConfig(new FileInputStream(
                AnalysisJcsegPlugin.getPluginSafeFile("jcseg.properties")));
        
        /* Load and apply the self-define arguments for Jcseg */
        for ( String key : settings.names() ) {
        	if ( key.startsWith("jcseg_") ) {
        		config.set(key.replace("jcseg_", "jcseg."), settings.get(key));
        	}
        }
        
        analyzer = new JcsegAnalyzer(this.getSegMode(), config, AnalysisJcsegPlugin.createSingletonDictionary(config));
    }

    protected abstract int getSegMode();
    
    @Override public JcsegAnalyzer get() 
    {
        return this.analyzer;
    }
    
}
