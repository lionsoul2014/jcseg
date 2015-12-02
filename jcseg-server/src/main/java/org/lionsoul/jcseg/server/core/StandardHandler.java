package org.lionsoul.jcseg.server.core;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.lionsoul.jcseg.server.GlobalResourcePool;

/**
 * jcseg server router handler
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class StandardHandler extends AbstractHandler
{
	/**
	 * global resource pool
	*/
	private GlobalResourcePool resourcePool = null;
	
	/**
	 * router 
	*/
	protected AbstractRouter router = null;
	
	/**
	 * contruct method
	 * 
	 * @param	resourcePool
	 * @param	router
	*/
	public StandardHandler(GlobalResourcePool resourcePool, AbstractRouter router)
	{
		this.resourcePool = resourcePool;
		this.router = router;
	}
	
	@Override
	public void handle(
			String target, Request baseRequest, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException 
	{
		/*
		 * parse the current request uri to get the UriEntry 
		 * then pass the UriEntry to the router to define the Controller handler class
		*/
		UriEntry uriEntry = UriEntry.parseRequestUri(request.getRequestURI());
		Class<? extends Controller> _class = router.getController(uriEntry);
		System.out.println(uriEntry.getController()+"#"+uriEntry.getMethod());
		
		try {
			/*
			 * create the controller and do the basic initialize work 
			 * and invoke the run method to process the request.
			*/
			Class<?>[] paramType = new Class[]{
					GlobalResourcePool.class,
					UriEntry.class, 
					Request.class, 
					HttpServletRequest.class, 
					HttpServletResponse.class
			};
			Constructor<?> constructor = _class.getConstructor(paramType);
			
			Object[] arguments = new Object[]{
					resourcePool, 
					uriEntry, 
					baseRequest, 
					request, 
					response
			};
			Controller controller = (Controller)constructor.newInstance(arguments);
			controller.run(uriEntry.getMethod());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		//mark the request has bean handler
		baseRequest.setHandled(true);
	}
}
