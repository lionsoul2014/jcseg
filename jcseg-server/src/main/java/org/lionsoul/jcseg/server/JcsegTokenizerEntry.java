package org.lionsoul.jcseg.server;

import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;

/**
 *  TokenizerEntry to save setting for Tokenizer object 
 * 
 * @author dongyado<dongyado@gmail.com>
 * */
public class TokenizerEntry {
    
    private JcsegTaskConfig config = null;
	private ADictionary     dic    = null;

	
	public TokenizerEntry( JcsegTaskConfig _config, ADictionary _dic )
	{
	    this.config = _config;
	    this.dic    = _dic;
	}
	
    public JcsegTaskConfig getConfig() {
        return config;
    }
    
    public void setConfig(JcsegTaskConfig config) {
        this.config = config;
    }
    
    public ADictionary getDic() {
        return dic;
    }
    
    public void setDic(ADictionary dic) {
        this.dic = dic;
    }
}
