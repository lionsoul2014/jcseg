package org.lionsoul.jcseg.server.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lionsoul.jcseg.util.IStringBuffer;

/**
 * json maker
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class JSONWriter 
{
    /**
     * json data map
    */
    private Map<String, Object> data = null;
    
    /**
     * quick lancher
     * 
     * @return    JsonMaker
    */
    public static JSONWriter create()
    {
        return new JSONWriter();
    }
    
    /**
     * construct method 
    */
    public JSONWriter()
    {
        data = new HashMap<String, Object>();
    }
    
    /**
     * put a new mapping with a string
     *
     * @param    key
     * @param    object
    */
    public JSONWriter put(String key, Object obj)
    {
        data.put(key, obj);
        return this;
    }
    
    /**
     * put a new mapping with a vector
     * 
     * @param    key
     * @param    vector
    */
    public JSONWriter put(String key, Object[] vector)
    {
        data.put(key, vector2JsonString(vector));
        return this;
    }
    
    /**
     * rewrite the toString
     * 
     * @return    String
    */
    @Override
    public String toString()
    {
        return map2JsonString(data);
    }
    
    /**
     * vector to json string
     * 
     * @param    vector
     * @return    String
    */
    @SuppressWarnings("unchecked")
    public static String vector2JsonString(Object[] vector)
    {
        IStringBuffer sb = new IStringBuffer();
        sb.append('[');
        for ( Object o : vector )
        {
            if ( o instanceof List<?> ) {
                sb.append(list2JsonString((List<Object>)o)).append(',');
            } else if (o instanceof Object[]) {
                sb.append(vector2JsonString((Object[])o)).append(',');
            } else if (o instanceof Map<?,?>) {
                sb.append(map2JsonString((Map<String, Object>)o)).append(',');
            } else if ((o instanceof Boolean) 
                    || (o instanceof Byte) || (o instanceof Short) 
                    || (o instanceof Integer) || (o instanceof Long) 
                    || (o instanceof Float) || (o instanceof Double)) {
                sb.append(o.toString()).append(',');
            } else {
                String v = o.toString();
                int last = v.length() - 1;
                
                // Avoid string like "[Error] there is a problem" treat as a array 
                // and "{error}: there is a problem" treat as object
                if ( v.length() > 1 
                        && ( (v.charAt(0) == '{' && v.charAt(last) == '}' )
                                || (v.charAt(0) == '[' && v.charAt(last) == ']' ) ) 
                ) {
                    sb.append(v).append(',');
                } else {
                    sb.append('"').append(v).append("\",");
                }
            }
        }
        
        if ( sb.length() > 1 ) {
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append(']');
        
        return sb.toString();
    }
    
    /**
     * list to json string
     * 
     * @param    list
     * @return    String
    */
    @SuppressWarnings("unchecked")
    public static String list2JsonString(List<Object> list)
    {
        IStringBuffer sb = new IStringBuffer();
        sb.append('[');
        for ( Object o : list )
        {
            if ( o instanceof List<?> ) {
                sb.append(list2JsonString((List<Object>)o)).append(',');
            } else if (o instanceof Object[]) {
                sb.append(vector2JsonString((Object[])o)).append(',');
            } else if (o instanceof Map<?,?>) {
                sb.append(map2JsonString((Map<String, Object>)o)).append(',');
            } else if ((o instanceof Boolean) 
                    || (o instanceof Byte) || (o instanceof Short) 
                    || (o instanceof Integer) || (o instanceof Long) 
                    || (o instanceof Float) || (o instanceof Double)) {
                sb.append(o.toString()).append(',');
            } else {
                String v = o.toString();
                int last = v.length() - 1;
                
                if ( v.length() > 1 
                        && ( (v.charAt(0) == '{' && v.charAt(last) == '}' )
                                || (v.charAt(0) == '[' && v.charAt(last) == ']' ) ) 
                ) {
                    sb.append(v).append(',');
                } else {
                    sb.append('"').append(v).append("\",");
                }
            }
        }
        
        if ( sb.length() > 1 ) {
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append(']');
        
        return sb.toString();
    }
    
    /**
     * map to json string
     * 
     * @param    map
     * @return    String
    */
    @SuppressWarnings("unchecked")
    public static String map2JsonString(Map<String, Object> map)
    {
        IStringBuffer sb = new IStringBuffer();
        sb.append('{');
        for ( Map.Entry<String, Object> entry : map.entrySet() )
        {
            sb.append('"').append(entry.getKey().toString()).append("\": ");
            
            Object obj = entry.getValue();
            if ( obj instanceof List<?> ) {
                sb.append(list2JsonString((List<Object>)obj)).append(',');
            } else if (obj instanceof Object[]) {
                sb.append(vector2JsonString((Object[])obj)).append(',');
            } else if (obj instanceof Map<?,?>) {
                sb.append(map2JsonString((Map<String, Object>)obj)).append(',');
            } else if ((obj instanceof Boolean) 
                    || (obj instanceof Byte) || (obj instanceof Short) 
                    || (obj instanceof Integer) || (obj instanceof Long) 
                    || (obj instanceof Float) || (obj instanceof Double)) {
                sb.append(obj.toString()).append(',');
            } else {
                String v = obj.toString();
                int last = v.length() - 1;
                
                if ( v.length() > 1 
                        && ( (v.charAt(0) == '{' && v.charAt(last) == '}' )
                                || (v.charAt(0) == '[' && v.charAt(last) == ']' ) ) 
                ) {
                    sb.append(v).append(',');
                } else {
                    sb.append('"').append(v).append("\",");
                }
            }
        }
        if ( sb.length() > 1 ) {
            sb.deleteCharAt(sb.length()-1);
        }
        sb.append('}');
        
        return sb.toString();
    }
    
}
