package org.lionsoul.jcseg.server.core;

import java.util.HashMap;
import java.util.Map;

/**
 * rest style router
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class DynamicRestRouter extends AbstractRouter
{
    /**
     * base package path for the controller 
    */
    private final String basePath;
    
    
    /**
     * standard path to controller mapping 
    */
    private final Map<String, Class<? extends Controller>> mapping;

    public DynamicRestRouter(String basePath, 
            Class<? extends Controller> defaultController) 
    {
        super(defaultController);
        
        this.basePath = basePath;
        mapping = new HashMap<>();
    }

    @Override
    public void addMapping(String path, Class<? extends Controller> _class) 
    {
        mapping.put(path, _class);
    }

    @Override
    public void removeMapping(String path) 
    {
        mapping.remove(path);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends Controller> getController(UriEntry uriEntry) 
    {
        Class<? extends Controller> controller = null;
        
        /*
         * check the global mapping first 
        */
        String requireUri = uriEntry.getRequestUri();
        if ( mapping.containsKey(requireUri) ) {
            controller = mapping.get(requireUri);
        } else {
            /*
             * uriEntry.getController to define the Controller class
             * and the uriEntry.getMethod to define which method to invoke 
            */
            String cClass = uriEntry.getController();
            String method = uriEntry.getMethod();
            if ( cClass != null && method != null ) {
                //build the class package path
                String _clsName = basePath + "." + cClass + "Controller";
                try {
                    Class<?> _class = Class.forName(_clsName);
                    if ( Controller.class.isAssignableFrom(_class) ) {
                        controller = (Class<? extends Controller>) _class;
                    }
                } catch (ClassNotFoundException ignored) {}
            }
        }
        
        return controller == null ? defaultController : controller;
    }

}
