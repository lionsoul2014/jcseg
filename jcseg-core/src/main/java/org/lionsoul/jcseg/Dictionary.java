package org.lionsoul.jcseg;

import java.util.HashMap;
//import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.lionsoul.jcseg.core.ADictionary;
import org.lionsoul.jcseg.core.ILexicon;
import org.lionsoul.jcseg.core.IWord;
import org.lionsoul.jcseg.core.JcsegTaskConfig;

//import com.webssky.jcseg.core.JHashMap;

/**
 * Dictionary class. <br />
 * 
 * @author	chenxin<chenxin619315@gmail.com>
 */
public class Dictionary extends ADictionary {
	
	/**hash table for the words*/
	private Map<String, IWord>[] dics = null;
	
	@SuppressWarnings("unchecked")
	public Dictionary( JcsegTaskConfig config, Boolean sync ) {
		super(config, sync);
		dics = new Map[ILexicon.T_LEN];
		if ( this.sync ) {
			for ( int j = 0; j < ILexicon.T_LEN; j++ ) 
				dics[j] = new ConcurrentHashMap<String, IWord>(16, 0.80F);
		} else {
			for ( int j = 0; j < ILexicon.T_LEN; j++ ) 
				dics[j] = new HashMap<String, IWord>(16, 0.80F);
		}
	}
	
	/**
	 * @see ADictionary#match(int, String)
	 */
	@Override
	public boolean match(int t, String key) {
		if ( t < 0 || t >= ILexicon.T_LEN ) return false; 
		return dics[t].containsKey(key);
	}

	/**
	 * @see ADictionary#add(int, String, int) 
	 */
	@Override
	public void add(int t, String key, int type) {
		if ( t < 0 || t >= ILexicon.T_LEN ) return; 
		if ( dics[t].get(key) == null )
			dics[t].put(key, new Word(key, type));
	}

	/**
	 * @see ADictionary#add(int, String, int, int) 
	 */
	@Override
	public void add(int t, String key, int fre, int type) {
		if (  t < 0 || t >= ILexicon.T_LEN  ) return;
		if ( dics[t].get(key) == null )
			dics[t].put(key, new Word(key, fre, type));
	}

	/**
	 * @see ADictionary#get(int, String) 
	 */
	@Override
	public IWord get(int t, String key) {
		if (  t < 0 || t >= ILexicon.T_LEN  ) return null; 
		return dics[t].get(key);
	}

	/**
	 * @see ADictionary#remove(int, String) 
	 */
	@Override
	public void remove(int t, String key) {
		if (  t < 0 || t >= ILexicon.T_LEN  ) return; 
		dics[t].remove(key);
	}
	
	/**
	 * @see ADictionary#size(int) 
	 */
	@Override
	public int size(int t) {
		if (  t < 0 || t >= ILexicon.T_LEN  ) return 0; 
		return dics[t].size();
	}
}
