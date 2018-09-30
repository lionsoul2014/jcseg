package org.lionsoul.jcseg.elasticsearch.index.analysis;

import java.io.FileInputStream;
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
    private final JcsegTaskConfig config;
    private final ADictionary dic;
    private final int mode;
    
    public JcsegTokenizerTokenizerFactory(
            IndexSettings indexSettings, Environment environment, String name, Settings settings) throws IOException {
        super(indexSettings, name, settings);
        
        config = new JcsegTaskConfig(new FileInputStream(CommonUtil.getPluginSafeFile("jcseg.properties")));
        config.setAutoload(false);          // disable the autoload of the lexicon
        mode = CommonUtil.getSegMode(settings, JcsegTaskConfig.SEARCH_MODE);
        dic  = DictionaryFactory.createSingletonDictionary(config);
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
