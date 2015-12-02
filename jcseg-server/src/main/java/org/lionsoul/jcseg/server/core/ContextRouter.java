package org.lionsoul.jcseg.server.core;

/**
 * context router - only when it perfect match 
*/
public class ContextRouter extends AbstractRouter
{

	public ContextRouter(Class<? extends Controller> defaultController) {
		super(defaultController);
	}

	@Override
	protected void addMapping(String path, Class<? extends Controller> _class) {
		
	}

	@Override
	protected void removeMapping(String path) {
		
	}

	@Override
	protected Class<? extends Controller> getController(UriEntry uriEntry) {
		return defaultController;
	}

}
