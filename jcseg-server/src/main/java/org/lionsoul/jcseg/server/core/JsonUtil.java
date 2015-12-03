package org.lionsoul.jcseg.server.core;

import java.util.List;

import org.lionsoul.jcseg.util.IStringBuffer;

/**
 * json util class
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class JsonUtil 
{
	/**
	 * array to string
	 * 
	 * @param	array
	 * @return	String
	*/
	@SuppressWarnings("unchecked")
	public static String vector2JsonArrayString(Object[] data)
	{
		IStringBuffer sb = new IStringBuffer();
		sb.append('[');
		for ( Object o : data )
		{
			String v = null;
			if (o instanceof List<?>) {
				v = list2JsonArrayString((List<Object>)o);
			} else if (o instanceof Object[]) {
				v = vector2JsonArrayString((Object[])o);
			} else {
				v = o.toString();
			}
			
			if ( v.charAt(0) == '{' || v.charAt(0) == '[' ) {
				sb.append(v).append(',');
			} else {
				sb.append('"').append(v).append("\",");
			}
		}
		
		if ( sb.length() > 1 ) {
			sb.deleteCharAt(sb.length()-1);
		}
		sb.append(']');
		
		return sb.toString();
	}
	
	/**
	 * list to string
	 * 
	 * @param	list
	 * @return	String
	*/
	@SuppressWarnings("unchecked")
	public static String list2JsonArrayString(List<Object> data)
	{
		IStringBuffer sb = new IStringBuffer();
		sb.append('[');
		for ( Object o : data )
		{
			String v = null;
			if ( o instanceof List<?> ) {
				v = list2JsonArrayString((List<Object>)o);
			} else if (o instanceof Object[]) {
				v = vector2JsonArrayString((Object[])o);
			} else {
				v = o.toString();
			}
			
			if ( v.charAt(0) == '{' || v.charAt(0) == '[' ) {
				sb.append(v).append(',');
			} else {
				sb.append('"').append(v).append("\",");
			}
		}
		
		if ( sb.length() > 1 ) {
			sb.deleteCharAt(sb.length()-1);
		}
		sb.append(']');
		
		return sb.toString();
	}
}
