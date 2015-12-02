package org.lionsoul.jcseg.server.core;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.lionsoul.jcseg.server.GlobalResourcePool;

/**
 * base Contoller class 
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public abstract class Controller 
{
	/**
	 * global resource pool
	*/
	protected GlobalResourcePool resourcePool;
	
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
	 * request UriEntry
	*/
	protected UriEntry uri;
	
	
	/**
	 * contruct method
	 * 
	 * @param	baseRequest
	 * @param	request
	 * @param	response
	 * @throws	IOException 
	*/
	public Controller(
			GlobalResourcePool resourcePool, UriEntry uriEntry, 
			Request baseRequest, HttpServletRequest request, 
			HttpServletResponse response) throws IOException
	{
		this.resourcePool = resourcePool;
		this.uri = uriEntry;
		this.baseRequest = baseRequest;
		this.request = request;
		this.response = response;
	}
	
	/**
	 * handle the current request
	 * 
	 * @param	method
	 * @throws IOException
	*/
	protected void run(String method) throws IOException
	{
		
	}
	
	/**
	 * get a String argument
	 * 
	 * @param	name
	 * @return	String
	*/
	public String getString(String name)
	{
		return request.getParameter(name);
	}
	
	/**
	 * get a integer arguments
	 * 
	 * @param	name
	 * @return	int
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
	 * @param	name
	 * @return	float
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
	 * @param	name
	 * @return	long
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
	 * @param	name
	 * @return	double
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
	 * @param	name
	 * @return	boolean
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
	 * global output protocol
	 * 
	 * @param	status
	 * @param	errcode
	 * @param	data
	 * @param	quote
	 * @throws  IOException 
	*/
	protected void response(
			boolean status, int errcode, String data, 
			boolean quote) throws IOException
	{
		PrintWriter output = response.getWriter();
		output.println("{");
		output.println("\"status\": " + status + ",");
		output.println("\"errcode\": " + errcode + ",");
		if (quote) output.println("\"data\": \"" + data + "\"");
		else output.println("\"data\": " + data);
		output.println("}");
	}
	
	protected void response(boolean status, int errcode, String data) throws IOException
	{
		response(status, errcode, data, true);
	}
	
}
