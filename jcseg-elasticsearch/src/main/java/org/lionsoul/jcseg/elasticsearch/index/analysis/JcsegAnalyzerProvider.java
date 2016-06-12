package org.lionsoul.jcseg.elasticsearch.index.analysis;

import java.io.File;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.elasticsearch.index.settings.IndexSettingsService;
import org.lionsoul.jcseg.analyzer.v5x.JcsegAnalyzer5X;
import org.lionsoul.jcseg.elasticsearch.util.CommonUtil;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;

/**
 * JcsegAnalyzerProvider
 * 
 * @author chenxin<chenxin619315@gmail.com>
 */
public class JcsegAnalyzerProvider extends AbstractIndexAnalyzerProvider<JcsegAnalyzer5X>
{
    /**default Jcseg tokenizer instance*/
    private final JcsegAnalyzer5X analyzer;
    
    @Inject
    public JcsegAnalyzerProvider(Index index, IndexSettingsService indexSettingsService, 
            Environment env, @Assisted String name, @Assisted Settings settings) 
    {
        super(index, indexSettingsService.getSettings(), name, settings);
        
        File proFile = new File(settings.get("config_file", "plugins/jcseg/jcseg.properties"));
        analyzer = proFile.exists() ? 
            new JcsegAnalyzer5X(CommonUtil.getSegMode(settings, JcsegTaskConfig.SEARCH_MODE), proFile.getPath()) : 
                new JcsegAnalyzer5X(CommonUtil.getSegMode(settings, JcsegTaskConfig.SEARCH_MODE));
    }
    
    @Override public JcsegAnalyzer5X get() 
    {
        return this.analyzer;
    }
    
}
