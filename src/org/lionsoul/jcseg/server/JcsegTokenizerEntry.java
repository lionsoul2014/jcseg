package org.lionsoul.jcseg.server;

import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;

/**
 *  TokenizerEntry to save setting for Tokenizer object 
 * 
 * @author dongyado<dongyado@gmail.com>
 * @author chenxin<chenxin619315@gmail.com>
*/
public class JcsegTokenizerEntry {
    
    private int algorithm;
    private JcsegTaskConfig config = null;
    private ADictionary dic = null;
    
    public JcsegTokenizerEntry(int algorithm, JcsegTaskConfig config, ADictionary dic )
    {
        this.algorithm = algorithm;
        this.config = config;
        this.dic = dic;
    }
    
    public JcsegTaskConfig getConfig()
    {
        return config;
    }
    
    public void setConfig(JcsegTaskConfig config)
    {
        this.config = config;
    }
    
    public ADictionary getDict()
    {
        return dic;
    }
    
    public void setDict(ADictionary dic)
    {
        this.dic = dic;
    }

    public int getAlgorithm()
    {
        return algorithm;
    }

    public void setAlgorithm(int algorithm)
    {
        this.algorithm = algorithm;
    }
    
}
