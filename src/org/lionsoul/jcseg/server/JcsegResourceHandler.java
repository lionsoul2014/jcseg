package org.lionsoul.jcseg.server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * resource handler  for jcseg server
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class JcsegResourceHandler extends AbstractHandler
{
    /**
     * default mine type mapping 
    */
    private static Map<String, String> mimeMap = null;
    static {
        mimeMap = new HashMap<String, String>();
        mimeMap.put("jpg",  "image/jpeg");
        mimeMap.put("jpeg", "image/jpeg");
        mimeMap.put("gif",  "image/gif");
        mimeMap.put("png",  "image/png");
        mimeMap.put("ico",  "image/x-icon");
        mimeMap.put("css",  "text/css");
        mimeMap.put("js",   "text/javascript");
        mimeMap.put("html", "text/html");
        mimeMap.put("shtml","text/html");
        mimeMap.put("xhtml","text/html");
        mimeMap.put("htm",  "text/html");
    }

    @Override
    public void handle(
            String target, 
            Request baseRequest, 
            HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException 
    {
        String requestUri = request.getRequestURI();
        int pos = requestUri.lastIndexOf('.');
        if ( pos > -1 ) {
            //interception for favicon.ico
            if ( requestUri.indexOf("favicon.ico") > -1 ) {
                requestUri = "/images/logo-x32.png";
            }
            
            /*
             * check the exitstence of the request resource 
            */
            InputStream is = this.getClass().getResourceAsStream("/res"+requestUri);
            if ( is == null ) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            /*
             * get the file extension and get its mime type 
             * and take the application/octet-stream as the default mime type
            */
            String ext = requestUri.substring(pos+1);
            String mimeType = mimeMap.get(ext);
            if ( mimeType == null ) {
                mimeType = "application/octet-stream";
            }
            
            response.setHeader("Content-Type", mimeType);
            ServletOutputStream sos = response.getOutputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            
            int len = 0;
            byte[] b = new byte[4096];
            while ( (len = bis.read(b)) > 0 ) {
                sos.write(b, 0, len);
            }
            
            sos.flush();
            baseRequest.setHandled(true);
        }
    }

}
