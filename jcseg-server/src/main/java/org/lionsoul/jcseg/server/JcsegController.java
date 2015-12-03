package org.lionsoul.jcseg.server;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.lionsoul.jcseg.server.core.Controller;
import org.lionsoul.jcseg.server.core.UriEntry;
import org.lionsoul.jcseg.util.IStringBuffer;

public class JcsegController extends Controller
{

	public JcsegController(
			GlobalProjectSetting setting,
			GlobalResourcePool resourcePool, 
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
	 * @param	status
	 * @param	errcode
	 * @param	data
	*/
	protected void response(boolean status, int errcode, String data )
	{		
		/*
		 * send the json content type and the charset 
		*/
		response.setContentType("application/json;charset="+setting.getCharset());
		
		IStringBuffer sb = new IStringBuffer();
		sb.append("{\n");
		sb.append("\"status\": ").append(status).append(",\n");
		sb.append("\"errcode\": ").append(errcode).append(",\n");
		sb.append("\"data\": ");
		if ( data.charAt(0) == '{' || data.charAt(0) == '[' ) {
			sb.append(data).append('\n');
		} else {
			sb.append('"').append(data).append("\"\n");
		}
		sb.append("}\n");
		
		output.println(sb.toString());
		
		//let the gc do its work
		sb = null;
	}
	
	/**
	 * global list output protocol
	 * 
	 * @param	status
	 * @param	errcode
	 * @param	data
	*/
	protected void response(boolean status, int errcode, List<Object> data)
	{
		IStringBuffer sb = new IStringBuffer();
		sb.append('[');
		for ( Object o : data )
		{
			sb.append('"').append(o.toString()).append("\",");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append(']');
		
		response(status, errcode, sb.toString());
		
		//let the gc do its work
		sb = null;
	}
	
	/**
	 * global Vector output protocol
	 * 
	 * @param	status
	 * @param	errcode
	 * @param	data
	*/
	protected void response(boolean status, int errcode, Object[] data)
	{
		IStringBuffer sb = new IStringBuffer();
		sb.append('[');
		for ( Object o : data )
		{
			sb.append('"').append(o.toString()).append("\",");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append(']');
		
		response(status, errcode, sb.toString());
		
		//let the gc do its work
		sb = null;
	}
	
	/**
	 * global map output protocol
	 * 
	 * @param	status
	 * @param	errcode
	 * @param	data
	*/
	protected void response(boolean status, int errcode, Map<String, Object> data)
	{
		IStringBuffer sb = new IStringBuffer();
		sb.append('{');
		for ( Map.Entry<String, Object> entry : data.entrySet() )
		{
			String value = entry.getValue().toString();
			sb.append('"').append(entry.getKey()).append("\": ");
			if ( value.charAt(0) == '{' || value.charAt(0) == '[' ) {
				sb.append(value).append(',');
			} else {
				sb.append('"').append(value).append("\",");
			}
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append('}');
		
		response(status, errcode, sb.toString());
		
		//let the gc do its work
		sb = null;
	}
	
}
