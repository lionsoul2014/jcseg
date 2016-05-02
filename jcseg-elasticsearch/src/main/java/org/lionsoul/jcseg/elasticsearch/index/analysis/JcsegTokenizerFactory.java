package org.lionsoul.jcseg.elasticsearch.index.analysis;

import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;
import org.elasticsearch.index.settings.IndexSettingsService;
import org.lionsoul.jcseg.analyzer.v5x.JcsegTokenizer;
import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.DictionaryFactory;
import org.lionsoul.jcseg.tokenizer.core.JcsegException;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;

import java.io.File;
import java.io.IOException;

/**
 * JcsegTokenizerFactory
 * 
 * @author chenxin<chenxin6193152@gmail.com>
 */
public class JcsegTokenizerFactory extends AbstractTokenizerFactory 
{
    private String seg_mode;
    private JcsegTaskConfig config;
    private ADictionary dic;

    @Inject
    public JcsegTokenizerFactory(Index index, 
            IndexSettingsService indexSettingsService, @Assisted String name, @Assisted Settings settings) 
    {
        super(index, indexSettingsService.getSettings(), name, settings);
        
        File proFile = new File(settings.get("config_file", "plugins/jcseg/jcseg.properties"));
        seg_mode = settings.get("seg_mode", "complex");
        config = proFile.exists() ? new JcsegTaskConfig(proFile.getPath()) : new JcsegTaskConfig();
        dic = DictionaryFactory.createSingletonDictionary(config);
    }
    
    @Override public Tokenizer create() 
    {
        int mode = JcsegTaskConfig.COMPLEX_MODE;
        if( seg_mode.equals("complex") ) {
            mode = JcsegTaskConfig.COMPLEX_MODE;
        } else if ( seg_mode.equals("simple") ) {
            mode = JcsegTaskConfig.SIMPLE_MODE;
        } else if( seg_mode.equals("detect") ) {
            mode = JcsegTaskConfig.DETECT_MODE;
        }
        
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
