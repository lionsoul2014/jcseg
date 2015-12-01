package org.lionsoul.jcseg.server;

/**
 * jcseg server configuration class
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class JcsegServerConfig 
{
	/**
	 * access host 
	*/
	private String host = null;
	
	/**
	 * server port
	*/
	private int port = 8080;
	
	/**
	 * http idle timeout in millseconds
	*/
	private long httpIdleTimeout = 30000;
	
	/**
	 * output buffer size 
	*/
	private int outputBufferSize = 32768;
	
	/**
	 * request header size 
	*/
	private int requestHeaderSize = 8192;
	
	/**
	 * response header size 
	*/
	private int responseHeaderSize = 8192;
	
	/**
	 * max thread size in the thread pool 
	*/
	private int maxThreadPoolSize = 200;
	
	/**
	 * max thread idle time in ms 
	*/
	private int threadIdleTimeout = 60000;
	
	
	public JcsegServerConfig()
	{
		
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public long getHttpIdleTimeout() {
		return httpIdleTimeout;
	}

	public void setHttpIdleTimeout(long idleTimeOut) {
		this.httpIdleTimeout = idleTimeOut;
	}

	public int getOutputBufferSize() {
		return outputBufferSize;
	}

	public void setOutputBufferSize(int outputBufferSize) {
		this.outputBufferSize = outputBufferSize;
	}

	public int getRequestHeaderSize() {
		return requestHeaderSize;
	}

	public void setRequestHeaderSize(int requestHeaderSize) {
		this.requestHeaderSize = requestHeaderSize;
	}

	public int getResponseHeaderSize() {
		return responseHeaderSize;
	}

	public void setResponseHeaderSize(int responseHeaderSize) {
		this.responseHeaderSize = responseHeaderSize;
	}

	public int getMaxThreadPoolSize() {
		return maxThreadPoolSize;
	}

	public void setMaxThreadPoolSize(int maxThreadPoolSize) {
		this.maxThreadPoolSize = maxThreadPoolSize;
	}

	public int getThreadIdleTimeout() {
		return threadIdleTimeout;
	}

	public void setThreadIdleTimeout(int maxThreadIdleTimeout) {
		this.threadIdleTimeout = maxThreadIdleTimeout;
	}
	
}
