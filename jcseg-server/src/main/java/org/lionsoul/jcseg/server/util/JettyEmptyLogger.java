package org.lionsoul.jcseg.server.util;

import org.eclipse.jetty.util.log.Logger;

public class JettyEmptyLogger implements Logger
{

    @Override
    public void debug(Throwable arg0)
    {
        
    }

    @Override
    public void debug(String arg0, Object... arg1)
    {
        
    }

    @Override
    public void debug(String arg0, long arg1)
    {
        
    }

    @Override
    public void debug(String arg0, Throwable arg1)
    {
        
    }

    @Override
    public Logger getLogger(String arg0)
    {
        return this;
    }

    @Override
    public String getName()
    {
        return "EmptyLogger";
    }

    @Override
    public void ignore(Throwable arg0)
    {
        
    }

    @Override
    public void info(Throwable arg0)
    {
        
    }

    @Override
    public void info(String arg0, Object... arg1)
    {
        
    }

    @Override
    public void info(String arg0, Throwable arg1)
    {
        
    }

    @Override
    public boolean isDebugEnabled()
    {
        return false;
    }

    @Override
    public void setDebugEnabled(boolean arg0)
    {
        
    }

    @Override
    public void warn(Throwable arg0)
    {
        
    }

    @Override
    public void warn(String arg0, Object... arg1)
    {
        
    }

    @Override
    public void warn(String arg0, Throwable arg1)
    {
        
    }

}
