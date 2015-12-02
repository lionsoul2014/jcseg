package org.lionsoul.jcseg.server.core;

import java.io.IOException;
import java.io.PrintWriter;

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
			UriEntry uriEntry, Request baseRequest, 
			HttpServletRequest request, HttpServletResponse response) throws IOException
	{
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
		if (quote) output.print("\"data\": \"" + data + "\"");
		else output.println("\"data\": " + data);
		output.println("}");
	}
	
	protected void response(boolean status, int errcode, String data) throws IOException
	{
		response(status, errcode, data, true);
	}
	
}
