package org.lionsoul.jcseg.tokenizer.core;

import java.io.IOException;
import java.lang.reflect.Constructor;

import org.lionsoul.jcseg.tokenizer.Dictionary;

/**
 * <p>
 * Dictionary Factory to create Dictionary instance
 * a path of the class that has extends the ADictionary class must be given first
 * </p>
 * 
 * @author    chenxin<chenxin619315@gmail.com>
 */
public class DictionaryFactory {
    
    private DictionaryFactory() {}
    
    /**
     * create a new ADictionary instance
     * 
     * @param     _class
     * @return    ADictionary
     */
    public static ADictionary createDictionary(
            Class<? extends ADictionary> _class, Class<?>[] paramType, Object[] args)
    {
        try {
            Constructor<?> cons = _class.getConstructor(paramType);
            return ( ( ADictionary ) cons.newInstance(args) );
        } catch ( Exception e ) {
            System.err.println("can't create the ADictionary instance " +
                    "with classpath ["+_class.getName()+"]");
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * create a default ADictionary instance of class com.webssky.jcseg.Dictionary
     * 
     * @param    config
     * @param    sync
     * @return    ADictionary
     */
    public static ADictionary createDefaultDictionary( 
            JcsegTaskConfig config, boolean sync ) 
    {
        ADictionary dic = createDictionary(Dictionary.class,
                    new Class[]{JcsegTaskConfig.class, Boolean.class},
                    new Object[]{config, sync});
        try {
            //load lexicon from more than one path.
            String[] lexpath = config.getLexiconPath();
            if ( lexpath == null ) 
                throw new IOException("Invalid lexicon path, " +
                        "make sure the JcsegTaskConfig is initialized.");
            
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
