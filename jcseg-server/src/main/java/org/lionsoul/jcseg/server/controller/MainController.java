package org.lionsoul.jcseg.server.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.lionsoul.jcseg.server.JcsegController;
import org.lionsoul.jcseg.server.core.GlobalResource;
import org.lionsoul.jcseg.server.core.ServerConfig;
import org.lionsoul.jcseg.server.core.UriEntry;

/**
 * common error handler
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class MainController extends JcsegController
{

    public MainController(
            ServerConfig config,
            GlobalResource resourcePool, 
            UriEntry uriEntry,
            Request baseRequest, 
            HttpServletRequest request,
            HttpServletResponse response) throws IOException
    {
        super(config, resourcePool, uriEntry, baseRequest, request, response);
    }

    @Override
    protected void run(String method) throws IOException
    {
        //interception for '/' to index.html
        if ( uri.getRequestUri().equals("/") ) {
            redirect("index.html");
            return;
        }
       
        this.response(
                false, 
                -1, 
                "No such controller found for " + request.getRequestURI()
        );
    }
}
