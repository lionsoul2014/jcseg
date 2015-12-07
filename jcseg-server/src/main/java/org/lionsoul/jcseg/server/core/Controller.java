package org.lionsoul.jcseg.server.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

/**
 * base Contoller class 
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public abstract class Controller 
{    
    /**
     * global server configuration 
    */
    protected ServerConfig config;
    
    /**
     * global resource pool
    */
    protected GlobalResource globalResource;
    
    /**
     * original base request 
    */
    protected Request baseRequest;
    
    /**
     * http servlet request 
    */
    protected HttpServletRequest request;
    
    /**
     * http servlet response 
    */
    protected HttpServletResponse response;
    
    /**
     * output
    */
    protected PrintWriter output = null;
    
    /**
     * request UriEntry
    */
    protected UriEntry uri;
    
    
    /**
     * contruct method
     * 
     * @param    config
     * @param    resourcePool
     * @param    uriEntry
     * @param    baseRequest
     * @param    request
     * @param    response
     * @throws    IOException 
    */
    public Controller(
            ServerConfig config,
            GlobalResource globalResource, 
            UriEntry uriEntry, 
            Request baseRequest, 
            HttpServletRequest request, 
            HttpServletResponse response) throws IOException
    {
        this.config = config;
        this.globalResource = globalResource;
        this.uri = uriEntry;
        this.baseRequest = baseRequest;
        this.request = request;
        this.response = response;
        
        init();
        /*
         * @Note: this line should after the init invoke 
        */
        this.output = response.getWriter();
    }
    
    /**
     * request initialize work
    */
    private void init()
    {
        //request.setCharacterEncoding(setting.getCharset());
        response.setCharacterEncoding(config.getCharset());
        response.setContentType("text/html;charset="+config.getCharset());
        response.setStatus(HttpServletResponse.SC_OK);
    }
    
    /**
     * handle the current request
     * 
     * @param    method
     * @throws IOException
    */
    protected void run(String method) throws IOException
    {
        
    }
    
    /**
     * get a String argument, 
     * encoding will be check and Re-encode maybe.
     * 
     * @param    name
     * @return    String
    */
    public String getString(String name)
    {
        return request.getParameter(name);
    }
    
    public String getEncodeString(String name)
    {
        String v = request.getParameter(name);
        if ( ! config.isDefaultCharset() ) {
            try {
                v = new String(
                    v.getBytes(ServerConfig.JETTY_DEFAULT_CHARSET), 
                    config.getCharset());
            } catch (UnsupportedEncodingException e) {
                //e.printStackTrace();
            }
        }
        
        return v;
    }
    
    /**
     * get a integer arguments
     * 
     * @param    name
     * @return    int
    */
    public int getInt(String name)
    {
        int val = 0;
        try {
            val = Integer.valueOf(request.getParameter(name));
        } catch (NumberFormatException e) {}
        
        return val;
    }
    
    public int getInt(String name, int val)
    {
        String v = request.getParameter(name);
        if ( v == null ) return val;
        return getInt(name);
    }
    
    /**
     * get a float arguments
     * 
     * @param    name
     * @return    float
    */
    public float getFloat(String name)
    {
        float fval = 0F;
        try {
            fval = Float.valueOf(request.getParameter(name));
        } catch (NumberFormatException e) {}
        
        return fval;
    }
    
    public float getFloat(String name, float val)
    {
        String v = request.getParameter(name);
        if ( v == null ) return val;
        return getFloat(name);
    }
    
    /**
     * get a long argument
     * 
     * @param    name
     * @return    long
    */
    public long getLong(String name)
    {
        long val = 0;
        try {
            val = Long.valueOf(request.getParameter(name));
        } catch (NumberFormatException e) {}
        
        return val;
    }
    
    public long getLong(String name, long val)
    {
        String v = request.getParameter(name);
        if ( v == null ) return val;
        return getLong(name);
    }
    
    /**
     * get a double argument
     * 
     * @param    name
     * @return    double
    */
    public double getDouble(String name)
    {
        double val = 0;
        try {
            val = Double.valueOf(request.getParameter(name));
        } catch (NumberFormatException e) {}
        
        return val;
    }
    
    public double getDouble(String name, double val)
    {
        String v = request.getParameter(name);
        if ( v == null ) return val;
        return getDouble(name);
    }
    
    /**
     * get a boolean argument
     * 
     * @param    name
     * @return    boolean
    */
    public boolean getBoolean(String name)
    {
        boolean val = false;
        try {
            val = Boolean.valueOf(request.getParameter(name));
        } catch (NumberFormatException e) {}
        
        return val;
    }
    
    public boolean getBoolean(String name, boolean val)
    {
        String v = request.getParameter(name);
        if ( v == null ) return val;
        return getBoolean(name);
    }
    
    /**
     * redirect to the specifield controller/method
     * 
     * @param    pathresponse
     * @throws IOException 
    */
    public void redirect(String path) throws IOException
    {
        //response.setHeader("Location", path.charAt(0)=='/' ? path : "/"+path);
        response.sendRedirect(path.charAt(0)=='/' ? path : "/"+path);
        output.close();
    }
}
