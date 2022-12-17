package org.lionsoul.jcseg.dic;

import java.io.File;

/**
 * AutoLoad file to describle the autoload configuration files
 * 
 * @author chenxin<chenxin619315@gmail.com>
 */
public class AutoLoadFile 
{
    /**
     * disk file File instance
     */
    private final File file;
    
    /**
     * last update unix stamp 
     */
    private long lastUpdateTime = 0L;
    
    public AutoLoadFile( String path )
    {
        file = new File(path);
        lastUpdateTime = 0L;
    }

    public File getFile() 
    {
        return file;
    }

    public long getLastUpdateTime()
    {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime)
    {
        this.lastUpdateTime = lastUpdateTime;
    }
}
