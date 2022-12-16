package org.lionsoul.jcseg.elasticsearch.index.analysis;

import java.io.IOException;
import java.nio.file.Files;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.lionsoul.jcseg.ISegment;
import org.lionsoul.jcseg.analyzer.JcsegAnalyzer;
import org.lionsoul.jcseg.elasticsearch.plugin.AnalysisJcsegPlugin;
import org.lionsoul.jcseg.segmenter.SegmenterConfig;

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
        
        final SegmenterConfig config = new SegmenterConfig(
                Files.newInputStream(AnalysisJcsegPlugin.getPluginSafeFile("jcseg.properties").toPath()));
        
        /* Load and apply the self-define arguments for Jcseg */
        for ( String key : settings.names() ) {
        	if ( key.startsWith("jcseg_") ) {
        		config.set(key.replace("jcseg_", "jcseg."), settings.get(key));
        	}
        }
        
        analyzer = new JcsegAnalyzer(getType(), config, AnalysisJcsegPlugin.createSingletonDictionary(config));
    }

    protected abstract ISegment.Type getType();
    
    @Override public JcsegAnalyzer get() 
    {
        return this.analyzer;
    }
    
}
