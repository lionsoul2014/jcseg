package org.lionsoul.jcseg.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;
import org.elasticsearch.index.settings.IndexSettingsService;
import org.lionsoul.jcseg.analyzer.v5x.JcsegTokenizer;
import org.lionsoul.jcseg.elasticsearch.util.CommonUtil;
import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.DictionaryFactory;
import org.lionsoul.jcseg.tokenizer.core.JcsegException;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;

import java.io.File;
import java.io.IOException;

/**
 * JcsegTokenizerFactory
 * 
 * @author chenxin<chenxin619315@gmail.com>
 */
public class JcsegTokenizerFactory extends AbstractTokenizerFactory 
{
    private JcsegTaskConfig config;
    private ADictionary dic;
    private int mode;

    @Inject
    public JcsegTokenizerFactory(Index index, 
            IndexSettingsService indexSettingsService, @Assisted String name, @Assisted Settings settings) 
    {
        super(index, indexSettingsService.getSettings(), name, settings);
        
        File proFile = new File(settings.get("config_file", CommonUtil.JcsegConfigFile));
        config = proFile.exists() ? new JcsegTaskConfig(proFile.getPath()) : new JcsegTaskConfig(true);
        mode = CommonUtil.getSegMode(settings, JcsegTaskConfig.SEARCH_MODE);
        dic = DictionaryFactory.createSingletonDictionary(config);
    }
    
    @Override public Tokenizer create() 
    {
        try {
            return new JcsegTokenizer(mode, config, dic);
        } catch (JcsegException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return null;
    }
    
}
