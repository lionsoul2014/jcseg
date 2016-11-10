package org.lionsoul.jcseg.server.core;

/**
 * server configuration class
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class ServerConfig 
{
    public static final String JETTY_DEFAULT_CHARSET = "iso-8859-1";
    
    /**
     * access host 
    */
    protected String host = null;
    
    /**
     * server port
    */
    protected int port = 8080;
    
    /**
     * http idle timeout in millseconds
    */
    protected long httpIdleTimeout = 30000;
    
    /**
     * output buffer size 
    */
    protected int outputBufferSize = 32768;
    
    /**
     * request header size 
    */
    protected int requestHeaderSize = 8192;
    
    /**
     * response header size 
    */
    protected int responseHeaderSize = 8192;
    
    /**
     * max thread size in the thread pool 
    */
    protected int maxThreadPoolSize = 200;
    
    /**
     * max thread idle time in ms 
    */
    protected int threadIdleTimeout = 60000;
    
    /**
     * static resouce base path 
    */
    protected String appBasePath = null;
    
    /**
     * default charset 
    */
    protected String charset = null;
    
    public ServerConfig()
    {
        charset = "utf-8";
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public long getHttpIdleTimeout()
    {
        return httpIdleTimeout;
    }

    public void setHttpIdleTimeout(long idleTimeOut)
    {
        this.httpIdleTimeout = idleTimeOut;
    }

    public int getOutputBufferSize()
    {
        return outputBufferSize;
    }

    public void setOutputBufferSize(int outputBufferSize)
    {
        this.outputBufferSize = outputBufferSize;
    }

    public int getRequestHeaderSize()
    {
        return requestHeaderSize;
    }

    public void setRequestHeaderSize(int requestHeaderSize)
    {
        this.requestHeaderSize = requestHeaderSize;
    }

    public int getResponseHeaderSize()
    {
        return responseHeaderSize;
    }

    public void setResponseHeaderSize(int responseHeaderSize)
    {
        this.responseHeaderSize = responseHeaderSize;
    }

    public int getMaxThreadPoolSize()
    {
        return maxThreadPoolSize;
    }

    public void setMaxThreadPoolSize(int maxThreadPoolSize)
    {
        this.maxThreadPoolSize = maxThreadPoolSize;
    }

    public int getThreadIdleTimeout()
    {
        return threadIdleTimeout;
    }

    public void setThreadIdleTimeout(int maxThreadIdleTimeout)
    {
        this.threadIdleTimeout = maxThreadIdleTimeout;
    }

    public String getAppBasePath()
    {
        return appBasePath;
    }

    public void setAppBasePath(String appBasePath)
    {
        this.appBasePath = appBasePath;
    }

    public String getCharset()
    {
        return charset;
    }

    public void setCharset(String charset)
    {
        this.charset = charset;
    }
    
    /**
     * check the server use the default iso-8859-1 chaset 
     * 
     * @return    boolean
    */
    public boolean isDefaultCharset()
    {
        return (charset.length() == JETTY_DEFAULT_CHARSET.length()
                && charset.charAt(0) == JETTY_DEFAULT_CHARSET.charAt(0));
    }
}
