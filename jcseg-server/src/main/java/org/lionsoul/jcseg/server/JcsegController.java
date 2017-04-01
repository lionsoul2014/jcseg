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
    public static final int STATUS_INTERNEL_ERROR = -1; //internal error
    public static final int STATUS_OK = 0;              //everything is fine
    public static final int STATUS_INVALID_ARGS = 1;    //invalid arguments
    public static final int STATUS_NO_SESSION = 2;      //no session
    public static final int STATUS_EMPTY_SETS = 3;      //empty sets
    public static final int STATUS_FAILED = 4;          //operation failed
    public static final int STATUS_DUPLICATE = 5;       //operation duplicate
    public static final int STATUS_ACCESS_DENY = 6;     //privileges deny
    
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
     * @param    code
     * @param    data
    */
    protected void response( int code, String data )
    {    
        /*
         * send the json content type and the charset 
        */
        response.setContentType("application/json;charset="+config.getCharset());
        
        JSONWriter json = JSONWriter.create()
                    .put("code", code)
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
     * @param    code
     * @param    data
    */
    protected void response(int code, List<Object> data)
    {
        response(code, JSONWriter.list2JsonString(data));
    }
    
    /**
     * global Vector output protocol
     * 
     * @param    code
     * @param    data
    */
    protected void response(int code, Object[] data)
    {
        response(code, JSONWriter.vector2JsonString(data));
    }
    
    /**
     * global map output protocol
     * 
     * @param    code
     * @param    data
    */
    protected void response(int code, Map<String, Object> data)
    {
        response(code, JSONWriter.map2JsonString(data));
    }
    
}
