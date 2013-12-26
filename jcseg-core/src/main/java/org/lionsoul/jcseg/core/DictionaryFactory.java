package org.lionsoul.jcseg.core;

import java.io.IOException;
import java.lang.reflect.Constructor;

/**
 * Dictionary Factory to create Dictionary instance . <br />
 * 		a path of the class that has extends the ADictionary
 * 	class must be given first. <br />
 * 
 * @author	chenxin<chenxin619315@gmail.com>
 */
public class DictionaryFactory {
	
	private DictionaryFactory() {}
	
	/**
	 * create a new ADictionary instance . <br />
	 * 
	 * @param 	__dicClass
	 * @return	ADictionary
	 */
	public static ADictionary createDictionary(
			String __dicClass, Class<?>[] paramType, Object[] args) {
		try {
			Class<?> _class = Class.forName(__dicClass);
			Constructor<?> cons = _class.getConstructor(paramType);
			return ( ( ADictionary ) cons.newInstance(args) );
		} catch ( Exception e ) {
			System.err.println("can't create the ADictionary instance " +
					"with classpath ["+__dicClass+"]");
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * create a default ADictionary instance of class
	 * 		com.webssky.jcseg.Dictionary . <br />
	 * 
	 * @see		Dictionary
	 * @return	ADictionary
	 */
	public static ADictionary createDefaultDictionary( JcsegTaskConfig config, boolean sync ) {
		ADictionary dic = createDictionary("org.lionsoul.jcseg.Dictionary",
					new Class[]{JcsegTaskConfig.class, Boolean.class},
					new Object[]{config, sync});
		try {
			//load lexicon from more than one path.
			String[] lexpath = config.getLexiconPath();
			if ( lexpath == null ) 
				throw new IOException("Invalid lexicon path, " +
						"make use the JcsegTaskConfig is initialized.");
			
			//load word item from all the directories.
			for ( String lpath : lexpath )
				dic.loadFromLexiconDirectory(lpath);
			if ( dic.getConfig().isAutoload() ) dic.startAutoload();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return dic;
	}
	
	public static ADictionary createDefaultDictionary( JcsegTaskConfig config ) {
		return createDefaultDictionary(config, config.isAutoload());
	}
}