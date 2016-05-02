package org.lionsoul.jcseg.server;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.lionsoul.jcseg.server.core.Controller;
import org.lionsoul.jcseg.server.core.GlobalResource;
import org.lionsoul.jcseg.server.core.JSONWriter;
import org.lionsoul.jcseg.server.core.ServerConfig;
import org.lionsoul.jcseg.server.core.UriEntry;

public class JcsegController extends Controller
{

    public JcsegController(
            ServerConfig setting,
            GlobalResource resourcePool, 
            UriEntry uriEntry,
            Request baseRequest, 
            HttpServletRequest request,
            HttpServletResponse response) throws IOException 
    {
        super(setting, resourcePool, uriEntry, baseRequest, request, response);
    }


    /**
     * global output protocol
     * 
     * @param    status
     * @param    errcode
     * @param    data
    */
    protected void response(boolean status, int errcode, String data )
    {    
        /*
         * send the json content type and the charset 
        */
        response.setContentType("application/json;charset="+config.getCharset());
        
        JSONWriter json = JSONWriter.create()
                .put("status", status)
                    .put("errcode", errcode)
                        .put("data", data);
        
        /*IStringBuffer sb = new IStringBuffer();
        sb.append("{\n");
        sb.append("\"status\": ").append(status).append(",\n");
        sb.append("\"errcode\": ").append(errcode).append(",\n");
        sb.append("\"data\": ");
        if ( data.charAt(0) == '{' || data.charAt(0) == '[' ) {
            sb.append(data).append('\n');
        } else {
            sb.append('"').append(data).append("\"\n");
        }
        sb.append("}\n");*/
        
        output.println(json.toString());
        output.flush();
        
        //let the gc do its work
        json = null;
    }
    
    /**
     * global list output protocol
     * 
     * @param    status
     * @param    errcode
     * @param    data
    */
    protected void response(boolean status, int errcode, List<Object> data)
    {
        response(status, errcode, JSONWriter.list2JsonString(data));
    }
    
    /**
     * global Vector output protocol
     * 
     * @param    status
     * @param    errcode
     * @param    data
    */
    protected void response(boolean status, int errcode, Object[] data)
    {
        response(status, errcode, JSONWriter.vector2JsonString(data));
    }
    
    /**
     * global map output protocol
     * 
     * @param    status
     * @param    errcode
     * @param    data
    */
    protected void response(boolean status, int errcode, Map<String, Object> data)
    {
        response(status, errcode, JSONWriter.map2JsonString(data));
    }
    
}
