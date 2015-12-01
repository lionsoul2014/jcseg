package org.lionsoul.jcseg.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.lionsoul.jcseg.server.controller.ErrorController;

/**
 * jcseg server router handler
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class RouterHandler extends AbstractHandler
{
	/**
	 * url pattern to controller mapping 
	*/
	Map<String, Class<? extends Controller>> router = null;
	
	public RouterHandler()
	{
		router = new HashMap<String, Class<? extends Controller>>();
	}
	
	/**
	 * add new mapping 
	 * 
	 * @param	path
	 * @param	_class
	*/
	public void addMapping(String path, Class<? extends Controller> _class)
	{
		router.put(path, _class);
	}
	
	/**
	 * remove the specifield mapping 
	 * 
	 * @param	path
	*/
	public void removeMapping(String path)
	{
		router.remove(path);
	}
	
	
	@Override
	public void handle(
			String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException 
	{
		String requestURI = request.getRequestURI();
		Class<? extends Controller> _class = ErrorController.class;
		if ( router.containsKey(requestURI) )
		{
			_class = router.get(requestURI);
		}
		
		
		try {
			/*
			 * create the controller and do the basic initialize work 
			*/
			Controller controller = _class.newInstance();
			controller.setRequest(request).setResponse(response);
			
			/*
			 * invoke the handler 
			*/
			String requestMethod = request.getMethod();
			if ( "GET".equals(requestMethod) ) controller.doGET();
			else if ("POST".equals(requestMethod)) controller.doPOST();
			else controller.doRequest();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		//mark the request has bean handler
		baseRequest.setHandled(true);
	}
}
