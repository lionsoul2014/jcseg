package org.lionsoul.jcseg.server.core;

import java.io.IOException;
import java.lang.reflect.Constructor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.lionsoul.jcseg.server.GlobalResourcePool;
import org.lionsoul.jcseg.server.GlobalProjectSetting;
import org.lionsoul.jcseg.server.JcsegServerConfig;

/**
 * jcseg server router handler
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class StandardHandler extends AbstractHandler
{
	/**
	 * server config 
	*/
	private JcsegServerConfig serverConfig = null;
	
	/**
	 * project global setting
	*/
	private GlobalProjectSetting setting = null;
	
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
	public StandardHandler(
			JcsegServerConfig serverConfig,
			GlobalProjectSetting setting,
			GlobalResourcePool resourcePool, AbstractRouter router)
	{
		this.serverConfig = serverConfig;
		this.setting = setting;
		this.resourcePool = resourcePool;
		this.router = router;
	}
	
	@Override
	public void handle(
			String target, Request baseRequest, 
			HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException 
	{
		String requestUri = request.getRequestURI();
		/*
		 * @Note: all the request that with point the in the path
		 * will consider to a resource request and will be handler by 
		 * the default resource handler from jetty. 
		*/
		if ( requestUri.length() > 1 && requestUri.indexOf('.') > -1 ) {
			//intercept the request for /favicon.ico
			if ( requestUri.equals("/favicon.ico") 
					&& serverConfig.getFavicon() != null ) 
			{
				byte[] favicon = serverConfig.getFavicon();
				response.setContentType("image/jpg");
				response.setContentLength(favicon.length);
				response.getOutputStream().write(favicon);
				baseRequest.setHandled(true);
			}
			
			//resource handler will handle it...
		} else {
			/*
			 * parse the current request uri to get the UriEntry 
			 * then pass the UriEntry to the router to define the Controller handler class
			*/
			UriEntry uriEntry = UriEntry.parseRequestUri(request.getRequestURI());
			Class<? extends Controller> _class = router.getController(uriEntry);
			//System.out.println(uriEntry.getController()+"#"+uriEntry.getMethod());
			
			try {
				/*
				 * create the controller and do the basic initialize work 
				 * and invoke the run method to process the request.
				*/
				Class<?>[] paramType = new Class[]{
						GlobalProjectSetting.class,
						GlobalResourcePool.class,
						UriEntry.class, 
						Request.class, 
						HttpServletRequest.class, 
						HttpServletResponse.class
				};
				Constructor<?> constructor = _class.getConstructor(paramType);
				
				Object[] arguments = new Object[]{
						setting,
						resourcePool, 
						uriEntry, 
						baseRequest, 
						request, 
						response
				};
				Controller controller = (Controller)constructor.newInstance(arguments);
				controller.run(uriEntry.getMethod());
			} catch (Exception e) {
				//e.printStackTrace();
			}
			
			/*
			 * mark the request has bean handler,
			 * so the request won't be handler by the other handler again 
			*/
			baseRequest.setHandled(true);
		}
	}
}
