package org.lionsoul.jcseg.analyzer.v5x;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;
import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.DictionaryFactory;
import org.lionsoul.jcseg.tokenizer.core.JcsegException;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;

/**
 * jcseg tokenizer factory class for solr
 * 
 * @author chenxin<chenxin619315@gmail.com>
 */
public class JcsegTokenizerFactory extends TokenizerFactory 
{
    
    private int mode;
    private JcsegTaskConfig config = null;
    private ADictionary dic = null;

    /**
     * set the mode arguments in the schema.xml 
     *     configuration file to change the segment mode for jcseg
     * 
     * @see TokenizerFactory#TokenizerFactory(Map<String, String)
     */
    public JcsegTokenizerFactory(Map<String, String> args)
    {
        super(args);
        
        String _mode = args.get("mode");
        if ( _mode == null ) {
            mode = JcsegTaskConfig.COMPLEX_MODE;
        } else {
            _mode = _mode.toLowerCase();
            if ( "simple".equals(_mode) ) {
                mode = JcsegTaskConfig.SIMPLE_MODE;
            } else if ( "detect".equals(_mode) ) {
                mode = JcsegTaskConfig.DETECT_MODE;
            } else if ( "search".equals(_mode) ) {
                mode = JcsegTaskConfig.SEARCH_MODE;
            } else {
                mode = JcsegTaskConfig.COMPLEX_MODE;
            }
        }
        
        //initialize the task configuration and the dictionary
        config = new JcsegTaskConfig(true);
        dic = DictionaryFactory.createSingletonDictionary(config);
    }
    
    public void setConfig( JcsegTaskConfig config ) 
    {
        this.config = config;
    }
    
    public void setDict( ADictionary dic ) 
    {
        this.dic = dic;
    }
    
    public JcsegTaskConfig getTaskConfig() 
    {
        return config;
    }
    
    public ADictionary getDict()
    {
        return dic;
    }

    @Override
    public Tokenizer create( AttributeFactory factory ) 
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
