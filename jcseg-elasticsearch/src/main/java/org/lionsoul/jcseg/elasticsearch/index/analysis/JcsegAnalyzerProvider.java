package org.lionsoul.jcseg.elasticsearch.index.analysis;

import java.io.File;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.lionsoul.jcseg.analyzer.JcsegAnalyzer;
import org.lionsoul.jcseg.elasticsearch.util.CommonUtil;

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
            IndexSettings indexSettings, Environment env, String name, Settings settings) 
    {
        super(indexSettings, name, settings);
        
        File proFile = new File(settings.get("config_file", CommonUtil.JcsegConfigFile));
        analyzer = proFile.exists() ? 
            new JcsegAnalyzer(this.getSegMode(), proFile.getPath()) : 
                new JcsegAnalyzer(this.getSegMode());
    }
    
    protected abstract int getSegMode();
    
    @Override public JcsegAnalyzer get() 
    {
        return this.analyzer;
    }
    
}
