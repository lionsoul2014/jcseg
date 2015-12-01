package org.lionsoul.jcseg.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;

/**
 * base Contoller class 
*/
public class Controller 
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
	 * contruct method
	 * 
	 * @param	baseRequest
	 * @param	request
	 * @param	response
	 * @throws	IOException 
	*/
	public Controller(
			Request baseRequest, HttpServletRequest request, 
			HttpServletResponse response) throws IOException
	{
		this.baseRequest = baseRequest;
		this.request = request;
		this.response = response;
	}
	
	public Controller()
	{
		//empty construct method
	}
	
	/**
	 * handle the current request
	 * 
	 * @throws IOException
	*/
	protected void doRequest() throws IOException
	{
		
	}
	
	/**
	 * handler the get request
	 * 
	 * @throws	IOException 
	*/
	protected void doGET() throws IOException
	{
		doRequest();
	}
	
	/**
	 * handler the post request 
	 * 
	 * @throws	IOException 
	*/
	protected void doPOST() throws IOException
	{
		doRequest();
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

	public HttpServletRequest getRequest() {
		return request;
	}

	public Controller setRequest(HttpServletRequest request) {
		this.request = request;
		return this;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public Controller setResponse(HttpServletResponse response) {
		this.response = response;
		return this;
	}
	
}
