package org.lionsoul.jcseg.server.core;

/**
 * rest style router
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class DynamicRestRouter extends AbstractRouter
{

	public DynamicRestRouter(Class<? extends Controller> defaultController) {
		super(defaultController);
	}

	@Override
	protected void addMapping(String path, Class<? extends Controller> _class) 
	{
		
	}

	@Override
	protected void removeMapping(String path) 
	{
		
	}
	
	@Override
	protected Class<? extends Controller> getController(UriEntry uriEntry) 
	{
		return defaultController;
	}

}
