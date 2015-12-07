package org.lionsoul.jcseg.server.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * global resource class
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public abstract class GlobalResource 
{
    /**
     * global resource mapping  
    */
    protected Map<Object, Object> resource = null;
    
    public GlobalResource()
    {
        resource = Collections.synchronizedMap(new HashMap<Object, Object>());
    }

    public Object getResource(String name) {
        return resource.get(name);
    }

    public void addResource(Object key, Object value) {
        resource.put(key, value);
    }
    
    public void removeResource(Object key)
    {
        resource.remove(key);
    }
    
}
