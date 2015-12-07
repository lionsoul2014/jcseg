package org.lionsoul.jcseg.analyzer.v5x;

/*import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.lionsoul.jcseg.analyzer.JcsegFilter;
import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.DictionaryFactory;
import org.lionsoul.jcseg.tokenizer.core.JcsegException;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;*/

/**
 * jcseg analyzer for lucene with version on or after 5.0
 * 
 * @author    chenxin<chenxin619315@gmail.com>
 */
/*public class JcsegAnalyzer5X extends Analyzer
{
    private int mode;
    private JcsegTaskConfig config = null;
    private ADictionary dic = null;
    
    public JcsegAnalyzer5X( int mode ) 
    {
        this.mode = mode;
        
        //initialize the task config and the dictionary
        config = new JcsegTaskConfig();
        dic = DictionaryFactory.createDefaultDictionary(config);
    }
    
    public JcsegAnalyzer5X( int mode, String proFile )
    {
        this.mode = mode;
        
        config = new JcsegTaskConfig(proFile);
        dic = DictionaryFactory.createDefaultDictionary(config);
    }
    
    public void setConfig( JcsegTaskConfig config ) 
    {
        this.config = config;
    }
    
    public void setDict( ADictionary dic ) {
        this.dic = dic;
    }
    
    public JcsegTaskConfig getTaskConfig() {
        return config;
    }
    
    public ADictionary getDict() {
        return dic;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) 
    {
        Tokenizer tokenizer;
        try {
            tokenizer = new JcsegTokenizer(mode, config, dic);
            return new TokenStreamComponents(tokenizer, new JcsegFilter(tokenizer));
        } catch (JcsegException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}*/
