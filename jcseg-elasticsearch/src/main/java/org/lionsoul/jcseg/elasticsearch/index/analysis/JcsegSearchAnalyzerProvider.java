package org.lionsoul.jcseg.elasticsearch.index.analysis;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;

import java.io.IOException;

/**
 * Jcseg Search Analyzer Provider
 * 
 * @author chenxin<chenxin619315@gmail.com>
 */
public class JcsegSearchAnalyzerProvider extends JcsegAnalyzerProvider
{
    public JcsegSearchAnalyzerProvider(IndexSettings indexSettings,
            Environment env, String name, Settings settings) throws IOException {
        super(indexSettings, env, name, settings);
    }

    @Override
    protected int getSegMode()
    {
        return JcsegTaskConfig.SEARCH_MODE;
    }
    
}
