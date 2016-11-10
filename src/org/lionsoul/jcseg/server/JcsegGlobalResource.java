package org.lionsoul.jcseg.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.lionsoul.jcseg.server.core.GlobalResource;
import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;

public class JcsegGlobalResource extends GlobalResource
{
    /**
     * dictionary pool 
    */
    private Map<String, ADictionary> dicPool = null;
    
    /**
     * JcsegTaskConfig pool 
    */
    private Map<String, JcsegTaskConfig> configPool = null;
    
    /**
     *  TokenizerEntry pool
     * */
    private Map<String, JcsegTokenizerEntry> tokenizerPool = null;
    
    
    /**
     * construct method 
    */
    public JcsegGlobalResource()
    {
        dicPool = Collections.synchronizedMap(new HashMap<String, ADictionary>());
        configPool = Collections.synchronizedMap(new HashMap<String, JcsegTaskConfig>());
        tokenizerPool = Collections.synchronizedMap(new HashMap<String, JcsegTokenizerEntry>());
    }
    
    
    /**
     * add a new tokenizer entry to entry pool
     * 
     * @param name
     * @param entry
     * */
    public void addTokenizerEntry( String name, JcsegTokenizerEntry entry)
    {
        tokenizerPool.put(name, entry);
    }
    
    
    /**
     *  remove a tokenizer entry from Tokenizer pool with specified name
     * 
     * @param name
     * */
    public void removeTokonizerEntry(String name)
    {
        tokenizerPool.remove( name );
    }

    
    /**
     * get tokenizer entry with specified name
     * 
     * @param  name
     * @return TokenizerEntry
     * */
    public JcsegTokenizerEntry getTokenizerEntry( String name)
    {
        return tokenizerPool.get(name);
    }
    
    
    /**
     * add a new Dictionary instance mapping
     * 
     * @param    name
     * @param    dic
    */
    public void addDict(String name, ADictionary dic)
    {
        dicPool.put(name, dic);
    }
    
    /**
     * remove specifield dictionary mapping
     * 
     * @param    name
    */
    public void removeDict(String name)
    {
        dicPool.remove(name);
    }
    
    /**
     * get the specifield dictionary
     * 
     * @param    name
     * @return    ADictionary
    */
    public ADictionary getDict(String name)
    {
        return dicPool.get(name);
    }
    
    /**
     * add a new JcsegTaskConfig mapping
     * 
     * @param    name
     * @param    config
    */
    public void addConfig(String name, JcsegTaskConfig config)
    {
        configPool.put(name, config);
    }
    
    /**
     * remove a specifield JcsegTaskConfig mapping
     * 
     * @param    name
    */
    public void removeConfig(String name)
    {
        configPool.remove(name);
    }
    
    /**
     * get the specifield config
     * 
     * @param    name
     * @return    JcsegTaskConfig
    */
    public JcsegTaskConfig getConfig(String name)
    {
        return configPool.get(name);
    }
}
