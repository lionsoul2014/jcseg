package org.lionsoul.jcseg.elasticsearch.util;

import org.elasticsearch.common.io.PathUtils;
import org.elasticsearch.common.settings.Settings;
import org.lionsoul.jcseg.elasticsearch.plugin.AnalysisJcsegPlugin;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;

import java.io.File;
import java.nio.file.Path;

public class CommonUtil 
{

    /**
     * get the sementation mode and default to COMPLEX_MODE
     * 
     * @param   settings
     * @return  int
    */
    public static int getSegMode(Settings settings)
    {
        return getSegMode(settings, JcsegTaskConfig.COMPLEX_MODE);
    }
    
    /**
     * get the segmentation mode
     * 
     * @param   settings
     * @param   default_mode
     * @return  int
    */
    public static int getSegMode(Settings settings, int default_mode)
    {
        String seg_mode = settings.get("seg_mode");
        if ( seg_mode == null ) {
            return default_mode;
        }
        
        int mode = default_mode;
        if( "complex".equals(seg_mode) ) {
            mode = JcsegTaskConfig.COMPLEX_MODE;
        } else if ( "simple".equals(seg_mode) ) {
            mode = JcsegTaskConfig.SIMPLE_MODE;
        } else if ( "detect".equals(seg_mode) ) {
            mode = JcsegTaskConfig.DETECT_MODE;
        } else if ( "search".equals(seg_mode) ) {
            mode = JcsegTaskConfig.SEARCH_MODE;
        } else if ( "nlp".equals(seg_mode) ){
            mode = JcsegTaskConfig.NLP_MODE;
        } else if ( "delimiter".equals(seg_mode) ) {
            mode = JcsegTaskConfig.DELIMITER_MODE;
        }
        
        return mode;
    }

    /**
     * Quick interface to get a safe file path
     *
     * @param   file
     */
    private static final String pluginBase = AnalysisJcsegPlugin.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    private static final Path safePath = PathUtils.get(new File(pluginBase).getParent()).toAbsolutePath();
    public static File getPluginSafeFile(String file)
    {
        return safePath.resolve(file).toFile();
    }
    
}
