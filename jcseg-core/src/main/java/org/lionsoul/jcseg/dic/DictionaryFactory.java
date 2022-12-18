package org.lionsoul.jcseg.dic;

import java.io.IOException;

import org.lionsoul.jcseg.IDictionary;
import org.lionsoul.jcseg.segmenter.SegmenterConfig;

/**
 * <p>
 * Dictionary Factory to create Dictionary instance
 * a path of the class that has extends the ADictionary class must be given first
 * </p>
 * 
 * @author  chenxin<chenxin619315@gmail.com>
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
     * create a default ADictionary instance:
     * 1. check the lexicon path and load the lexicons
     *  if it is null and default to load the lexicon in the classpath
     * 2. check and start the autoload settings of the dictionary
     * 
     * @param   config
     * @param   sync
     * @param   loadDic whether check and load the lexicon
     * @return  IDictionary
     */
    public static ADictionary createDefaultDictionary( 
            SegmenterConfig config, boolean sync, boolean loadDic ) 
    {
        final ADictionary dic = IDictionary.HASHMAP.factory.create(config, sync);
        if (!loadDic) {
            return dic;
        }
        
        try {
            /*
             * @Note: updated at 2016/07/07
             * 
             * check and load all the lexicons with more than one path
             * if specified none lexicon paths (config.getLexiconPath() is null)
             * And we directly load the default lexicons that in the class path
            */
            String[] lexPath = config.getLexiconPath();
            if ( lexPath == null ) {
            	dic.loadClassPath();
            } else {
                for ( String lPath : lexPath) dic.loadDirectory(lPath);
                if ( config.isAutoload() ) dic.startAutoload();
            }

            /*
             * added at 2017/06/10
             * check and reset synonyms net of the current Dictionary 
            */
            dic.resetSynonymsNet();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return dic;
    }
    
    /**
     * create the ADictionary according to the JcsegTaskConfig
     * check and load the lexicon by default
     * 
     * @param   config
     * @return  ADictionary
    */
    public static ADictionary createDefaultDictionary(SegmenterConfig config)
    {
        return createDefaultDictionary(config, true);
    }
    
    /**
     * create the ADictionary according to the JcsegTaskConfig
     * 
     * @param   config
     * @param   loadDic
     * @return  ADictionary
    */
    public static ADictionary createDefaultDictionary(SegmenterConfig config, boolean loadDic)
    {
        return createDefaultDictionary(config, config.isAutoload(), loadDic);
    }
    
    /**
     * create a singleton ADictionary object according to the JcsegTaskConfig
     * check and load the lexicon by default
     * 
     * @param   config
     * @return  ADictionary
    */
    public static ADictionary createSingletonDictionary(SegmenterConfig config)
    {
        return createSingletonDictionary(config, true);
    }
    
    /**
     * create a singleton ADictionary object according to the SegmentConfig
     * 
     * @param   config
     * @param   loadDic
     * @return  ADictionary
    */
    public static ADictionary createSingletonDictionary(SegmenterConfig config, boolean loadDic)
    {
        synchronized (LOCK) {
            if ( singletonDic == null ) {
                singletonDic = createDefaultDictionary(config, loadDic);
            }
        }
        
        return singletonDic;
    }
    
}
