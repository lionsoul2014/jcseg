package org.lionsoul.jcseg.elasticsearch.index.analysis;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;
import org.lionsoul.jcseg.analyzer.JcsegTokenizer;
import org.lionsoul.jcseg.elasticsearch.util.CommonUtil;
import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.DictionaryFactory;
import org.lionsoul.jcseg.tokenizer.core.JcsegException;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;

public class JcsegTokenizerTokenizerFactory extends AbstractTokenizerFactory
{
    private JcsegTaskConfig config;
    private ADictionary dic;
    private int mode;
    
    public JcsegTokenizerTokenizerFactory(
            IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(indexSettings, name, settings);
        
        File proFile = new File(settings.get("config_file", CommonUtil.JcsegConfigFile));
        config = proFile.exists() ? new JcsegTaskConfig(proFile.getPath()) : new JcsegTaskConfig(true);
        mode = CommonUtil.getSegMode(settings, JcsegTaskConfig.SEARCH_MODE);
        dic  = DictionaryFactory.createSingletonDictionary(config);
    }

    @Override
    public Tokenizer create() 
    {
        try {
            return new JcsegTokenizer(mode, config, dic);
        } catch (JcsegException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
