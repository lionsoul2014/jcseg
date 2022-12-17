package org.lionsoul.jcseg.opensearch.index.analysis;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.lionsoul.jcseg.ISegment;
import org.lionsoul.jcseg.analyzer.JcsegAnalyzer;
import org.lionsoul.jcseg.opensearch.plugin.AnalysisJcsegPlugin;
import org.lionsoul.jcseg.segmenter.SegmenterConfig;
import org.opensearch.common.inject.Inject;
import org.opensearch.common.settings.Settings;
import org.opensearch.env.Environment;
import org.opensearch.index.IndexSettings;
import org.opensearch.index.analysis.AbstractIndexAnalyzerProvider;

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
