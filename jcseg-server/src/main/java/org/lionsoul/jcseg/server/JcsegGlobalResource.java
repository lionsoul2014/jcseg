package org.lionsoul.jcseg.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.lionsoul.jcseg.dic.ADictionary;
import org.lionsoul.jcseg.segmenter.SegmenterConfig;
import org.lionsoul.jcseg.server.core.GlobalResource;

public class JcsegGlobalResource extends GlobalResource
{
    /**
     * dictionary pool 
    */
    private final Map<String, ADictionary> dicPool;
    
    /**
     * SegmenterConfig pool
    */
    private final Map<String, SegmenterConfig> configPool;
    
    /**
     *  TokenizerEntry pool
     * */
    private final Map<String, JcsegTokenizerEntry> tokenizerPool;
    
    
    /**
     * construct method 
    */
    public JcsegGlobalResource()
    {
        dicPool = Collections.synchronizedMap(new HashMap<>());
        configPool = Collections.synchronizedMap(new HashMap<>());
        tokenizerPool = Collections.synchronizedMap(new HashMap<>());
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
    public void addConfig(String name, SegmenterConfig config)
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
    public SegmenterConfig getConfig(String name)
    {
        return configPool.get(name);
    }
}
