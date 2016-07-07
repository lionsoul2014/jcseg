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
public class DictionaryFactory 
{
    /**
     * singleton lock 
    */
    private final static Object LOCK = new Object();
    
    /**
     * singleton dictionary object 
    */
    private static ADictionary singletonDic = null;
    
    
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
     * @param   config
     * @param   sync
     * @return  ADictionary
     */
    public static ADictionary createDefaultDictionary( 
            JcsegTaskConfig config, boolean sync ) 
    {
        ADictionary dic = createDictionary(
            Dictionary.class,
            new Class[]{JcsegTaskConfig.class, Boolean.class},
            new Object[]{config, sync}
        );
        
        try {
            /*
             * @Note: updated at 2016/07/07
             * 
             * check and load all the lexicons with more than one path
             * if specified none lexicon paths (config.getLexiconPath() is null)
             * And we directly load the default lexicons that in the class path
            */
            String[] lexpath = config.getLexiconPath();
            if ( lexpath == null ) {
                dic.loadClassPath();
            } else {
                for ( String lpath : lexpath )      dic.loadDirectory(lpath);
                if ( dic.getConfig().isAutoload() ) dic.startAutoload();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return dic;
    }
    
    public static ADictionary createDefaultDictionary(JcsegTaskConfig config)
    {
        return createDefaultDictionary(config, config.isAutoload());
    }
    
    /**
     * create a singleton ADictionary object
     * 
     * @return  ADictionary
    */
    public static ADictionary createSingletonDictionary(JcsegTaskConfig config)
    {
        synchronized (LOCK) {
            if ( singletonDic == null ) {
                singletonDic = createDefaultDictionary(config);
            }
        }
        
        return singletonDic;
    }
}
