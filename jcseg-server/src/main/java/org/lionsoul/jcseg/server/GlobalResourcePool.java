package org.lionsoul.jcseg.server;

import java.util.HashMap;
import java.util.Map;

import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;

/**
 * global resource pool that manager the
 * dictionray, configuration, ISegment instance of jcseg.
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class GlobalResourcePool 
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
	private Map<String, TokenizerEntry> tokenizerPool = null;
	
	
	/**
	 * construct method 
	*/
	public GlobalResourcePool()
	{
		dicPool = new HashMap<String, ADictionary>();
		configPool = new HashMap<String, JcsegTaskConfig>();
		tokenizerPool = new HashMap<String, TokenizerEntry>();
	}
	
	
	/**
	 * add a new tokenizer entry to entry pool
	 * 
	 * @param name
	 * @param entry
	 * */
	public void addTokenizerEntry( String name, TokenizerEntry entry)
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
	public TokenizerEntry getTokenizerEntry( String name)
	{
	    return tokenizerPool.get(name);
	}
	
	
	/**
	 * add a new Dictionary instance mapping
	 * 
	 * @param	name
	 * @param	dic
	*/
	public void addDict(String name, ADictionary dic)
	{
		dicPool.put(name, dic);
	}
	
	/**
	 * remove specifield dictionary mapping
	 * 
	 * @param	name
	*/
	public void removeDict(String name)
	{
		dicPool.remove(name);
	}
	
	/**
	 * get the specifield dictionary
	 * 
	 * @param	name
	 * @return	ADictionary
	*/
	public ADictionary getDic(String name)
	{
		return dicPool.get(name);
	}
	
	/**
	 * add a new JcsegTaskConfig mapping
	 * 
	 * @param	name
	 * @param	config
	*/
	public void addConfig(String name, JcsegTaskConfig config)
	{
		configPool.put(name, config);
	}
	
	/**
	 * remove a specifield JcsegTaskConfig mapping
	 * 
	 * @param	name
	*/
	public void removeConfig(String name)
	{
		configPool.remove(name);
	}
	
	/**
	 * get the specifield config
	 * 
	 * @param	name
	 * @return	JcsegTaskConfig
	*/
	public JcsegTaskConfig getConfig(String name)
	{
		return configPool.get(name);
	}
}
