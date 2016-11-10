package org.lionsoul.jcseg.server.core;

import java.util.ArrayList;
import java.util.List;

import org.lionsoul.jcseg.util.IStringBuffer;

public class UriEntry 
{
    /**
     * the original request uri
    */
    private String requestUri;
    
    /**
     * all parts of the request uri 
    */
    private List<String> parts = null;
    
    /**
     * the controller name 
    */
    private String controller = null;
    
    /**
     * the method name 
    */
    private String method = null;
    
    /**
     * parse a given request uri to generate a 
     * new UriEntry instance and return it
     *  
     * @param    requestUri
     * @return    UriEntry
    */
    public static UriEntry parseRequestUri(String requestUri)
    {
        return new UriEntry(requestUri);
    }
    
    /**
     * construct method 
     * 
     * @param    requestUri
    */
    public UriEntry(String requestUri)
    {
        this.requestUri = requestUri;
        process();
    }
    
    /**
     * do the request uri process 
    */
    private void process()
    {
        if (requestUri.length() > 1) {
            parts = new ArrayList<String>(10);
            for ( int i = 1; i < requestUri.length(); ) {
                int sIdx = i;
                int eIdx = requestUri.indexOf('/', sIdx + 1);
                
                //not matched or reach the end
                if ( eIdx == -1 ) {
                    parts.add(requestUri.substring(sIdx));
                    break;
                } 
                
                parts.add(requestUri.substring(sIdx, eIdx));
                i = eIdx + 1;
            }
            
            /*
             * check and add a empty method name
             * with request style like /tokenizer/
            */
            if ( requestUri.charAt(requestUri.length()-1) == '/' ) {
                parts.add("");
            }
            
            int length = parts.size();
            if ( length > 1 ) {
                IStringBuffer sb = new IStringBuffer();
                for ( int i = 0; i < length - 1; i++ ) {
                    int l = sb.length();
                    sb.append(parts.get(i));
                    
                    //make sure the first letter is uppercase
                    char chr = sb.charAt(l);
                    if ( chr >= 90 ) {
                        chr -= 32;
                        sb.set(l, chr);
                    }
                }
                
                controller = sb.toString();
            }
            
            method = parts.get(length-1);
        }
    }
    
    /**
     * the specifiled part of the request uri 
     * 
     * @param    idx
    */
    public String get(int idx)
    {
        if ( idx < 0 || idx >= parts.size() ) {
            throw new IndexOutOfBoundsException();
        }
        
        return parts.get(idx);
    }
    
    /**
     * get the length of the parts
     * 
     * @return    int
    */
    public int getLength()
    {
        return parts == null ? 0 : parts.size();
    }

    public String getController()
    {
        return controller;
    }

    public void setController(String controller)
    {
        this.controller = controller;
    }

    public String getMethod()
    {
        return method;
    }

    public void setMethod(String method)
    {
        this.method = method;
    }

    public String getRequestUri()
    {
        return requestUri;
    }

    public void setRequestUri(String requestUri)
    {
        this.requestUri = requestUri;
    }
    
}
