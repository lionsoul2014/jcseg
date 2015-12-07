package org.lionsoul.jcseg.tokenizer.core;

import java.io.File;

/**
 * AutoLoad file to describle the autoload configration files
 * 
 * @author chenxin<chenxin619315@gmail.com>
 */
public class AutoLoadFile 
{
    /**
     * disk file File instance
     */
    private File file = null;
    
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

    public AutoLoadFile setFile(File file) 
    {
        this.file = file;
        return this;
    }
    
    public long getLastUpdateTime() 
    {
        return lastUpdateTime;
    }

    public AutoLoadFile setLastUpdateTime(long lastUpdateTime) 
    {
        this.lastUpdateTime = lastUpdateTime;
        return this;
    }
}
