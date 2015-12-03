package org.lionsoul.jcseg.server;

/**
 * global project configuration class.
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class GlobalProjectSetting 
{
	/**
	 * default charset setting for jetty 
	*/
	public static final String JETTY_DEFAULT_CHASET = "iso-8859-1";
	
	/**
	 * main content encoding charset 
	*/
	private String charset = null;
	
	public GlobalProjectSetting()
	{
		charset = "utf-8";
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}
	
	/**
	 * check if we use the default charset 
	 * 
	 * @return	boolean
	*/
	public boolean isDefaultCharset()
	{
		return (charset.length() == JETTY_DEFAULT_CHASET.length() 
				&& charset.charAt(0) == JETTY_DEFAULT_CHASET.charAt(0));
	}
}
