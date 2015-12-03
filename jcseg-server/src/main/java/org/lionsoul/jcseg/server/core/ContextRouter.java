package org.lionsoul.jcseg.server.core;

import java.util.HashMap;
import java.util.Map;

/**
 * context router - only when it perfect match 
 * 	this router implements paths map a/b, a/b/* to a controller class
 * 
 * @author dongyado<dongyado@gmail.com>
*/
public class ContextRouter extends AbstractRouter
{
	private Map<String, Class<? extends Controller>> maps	 	= null;
	private Map<String, Class<? extends Controller>> matches 	= null;
	
	
	private static int MAP_PATH_TYPE = 1;
	private static int MATCH_PATH_TYPE = 2;
	
	public ContextRouter(Class<? extends Controller> defaultController) 
	{
		super(defaultController);
		
		maps 	= new HashMap<String, Class<? extends Controller>>();
		matches = new HashMap<String, Class<? extends Controller>>();
	}
	
	
	/** 
	 *  path entry class
	 * */
	private class PathEntry 
	{
		public int type 	= 0; // 1 - 100% match for map, 2 - pattern match for matches
		public String key 	= null;
//		public HashMap<String, String> params = null;
//		
//		public PathEntry(){}
//		
		public PathEntry(int _type, String _key)
		{
			this.type = _type;
			this.key  = _key;
		}
	}
	
	
	/**
	 *  get the final key with specified path
	 *  
	 *  paths 	: return:
	 *  /a/b  	:  /a/b
	 *  /a/*  	:  /a/ 
	 *  /a/b/c 	:  /a/b/c
	 *  /a/b/* 	:  /a/b/
	 *  /a/b/   :  /a/b/
	 * */
	private PathEntry getPathEntry(UriEntry uri)
	{
		PathEntry  pathEntry = new PathEntry( ContextRouter.MAP_PATH_TYPE, "default");
		
		
		int length = uri.getLength();
		// like /a or /
		if ( length < 2) return pathEntry;
	

		String requestUri = uri.getRequestUri();
		
		String last_str = uri.get( length - 1 );
		if ( last_str.equals("*") || last_str.equals("") )
		{
//			StringBuffer buffer = new StringBuffer();
//			buffer.append('/');
//			
//			for(int i = 0; i < length - 1; i++)
//			{
//				buffer.append(uri.get(i) + "/");
//			}
			
//			position of  last / charactor
			int lastPosition = requestUri.lastIndexOf('/');

			pathEntry.type = ContextRouter.MATCH_PATH_TYPE;
			pathEntry.key  = requestUri.substring(0, lastPosition + 1);
			
			// @TODO maybe we should get parameters to pathEntry
		} else {
			pathEntry.key  = requestUri;
		}
		
		return pathEntry;
	}

	
	@Override
	public void addMapping(String path, Class<? extends Controller> _class) 
	{
		UriEntry uri = new UriEntry(path);
		PathEntry entry = this.getPathEntry(uri);
		
		if ( entry.type == ContextRouter.MAP_PATH_TYPE) {
			if (entry.key.equals("default"))
				_class = defaultController;
			
			maps.put( entry.key, _class );
		} 
		else if ( entry.type == ContextRouter.MATCH_PATH_TYPE) 
		{
			matches.put( entry.key, _class );
		}
		
		//System.out.println(entry.type +":"+entry.key);
	}

	
	@Override
	public void removeMapping(String path) 
	{
		UriEntry uri = new UriEntry(path);
		PathEntry pathEntry = this.getPathEntry(uri);
		
		if ( pathEntry.type == ContextRouter.MAP_PATH_TYPE)
		{
			maps.remove(pathEntry.key);
		}
		else if ( pathEntry.type == ContextRouter.MATCH_PATH_TYPE)
		{
			matches.remove(pathEntry.key);
		}
	}

	
	@Override
	public Class<? extends Controller> getController(UriEntry uriEntry) 
	{
		PathEntry pathEntry = this.getPathEntry(uriEntry);
		Class<? extends Controller> controller = null;
		
		
		if ( pathEntry.type == ContextRouter.MAP_PATH_TYPE)
		{
			controller = maps.get(pathEntry.key);
		}
		
		// if cannot find the controller from maps.  
		// we try it from matches, even if its type is MAP_PATH_TYPE
		// and of course type of MATCH_PATH_TYPE should get controller from matches too.
		if ( controller == null ||  pathEntry.type == ContextRouter.MATCH_PATH_TYPE)
		{
			String key = uriEntry.getRequestUri();
			
			int lastPosition = key.lastIndexOf('/');
			
			while( lastPosition != -1)
			{
				key 		= key.substring(0, lastPosition);
				controller 	= matches.get(key + '/');
				
				if (controller != null) {
					// @note maybe we can store the result key and controller
					//  to another map to improve performance
					break;
				}
				lastPosition = key.lastIndexOf('/');
			}
		}

		return controller != null ? controller : defaultController;
	}
}
