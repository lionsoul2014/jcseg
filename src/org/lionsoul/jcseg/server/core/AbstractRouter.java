package org.lionsoul.jcseg.server.core;


/**
 * abstract router
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public abstract class AbstractRouter 
{
    /**
     * the default controller 
    */
    protected Class<? extends Controller> defaultController = null;
    
    /**
     * construct method
     * 
     * @param    defualtController
    */
    public AbstractRouter(Class<? extends Controller> defaultController)
    {
        this.defaultController = defaultController;
    }
    
    /**
     * add new mapping 
     * 
     * @param    path
     * @param    _class
    */
    public abstract void addMapping(String path, Class<? extends Controller> _class);
    
    /**
     * remove the specifield mapping 
     * 
     * @param    path
    */
    public abstract void removeMapping(String path);
    
    /**
     * get controller
     * 
     * @param    uriEntry
     * @return    Class<? extends Controller>
    */
    public abstract Class<? extends Controller> getController(UriEntry uriEntry);
}
