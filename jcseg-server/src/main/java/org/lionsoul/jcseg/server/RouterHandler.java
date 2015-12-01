package org.lionsoul.jcseg.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.lionsoul.jcseg.json.JSONObject;

/**
 * jcseg server router handler
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class RouterHandler extends AbstractHandler
{
	@Override
	public void handle(
			String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException 
	{
		JSONObject json = new JSONObject();
		json.put("errcode", 0);
		json.put("data", "this is the router handler");
		
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		
		PrintWriter out = response.getWriter();
		out.println(json.toString());
		
		//mark the request has bean handler
		baseRequest.setHandled(true);
	}
}
