package org.lionsoul.jcseg.server;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.lionsoul.jcseg.server.controller.KeyphraseController;
import org.lionsoul.jcseg.server.controller.KeywordsController;
import org.lionsoul.jcseg.server.controller.SentenceController;
import org.lionsoul.jcseg.server.controller.SummaryController;
import org.lionsoul.jcseg.server.controller.TokenizerController;

/**
 * Jcseg RESTful api server
 * 
 * @author chenxin<chenxin619315@gmail.com>
*/
public class JcsegServer 
{
	/**
	 * jcseg server config 
	*/
	private JcsegServerConfig config;
	
	/**
	 * jetty server instance 
	*/
	private Server server;
	
	/**
	 * construct method
	 * 
	 * @param	config
	*/
	public JcsegServer(JcsegServerConfig config)
	{
		this.config = config;
		this.init();
	}
	
	/**
	 * initialize the server and register the basic context handler
	*/
	private void init()
	{
		//setup thread pool
		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setMaxThreads(config.getMaxThreadPoolSize());
		threadPool.setIdleTimeout(config.getThreadIdleTimeout());
		
		server = new Server(threadPool);
		
		//setup the http configuration
		HttpConfiguration http_config = new HttpConfiguration();
		http_config.setOutputBufferSize(config.getOutputBufferSize());
		http_config.setRequestHeaderSize(config.getRequestHeaderSize());
		http_config.setResponseHeaderSize(config.getRequestHeaderSize());
		http_config.setSendServerVersion(false);
		http_config.setSendDateHeader(false);
		
		//setup the connector
		ServerConnector connector = new ServerConnector(
			server, 
			new HttpConnectionFactory(http_config)
		);
		connector.setPort(config.getPort());
		connector.setHost(config.getHost());
		connector.setIdleTimeout(config.getHttpIdleTimeout());
		server.addConnector(connector);
	}
	
	/**
	 * register handler service 
	*/
	public JcsegServer registerHandler()
	{
		/*
		 * yet, i am going to rewrite the path to handler mapping mechanism
		 * check the Router handler for more info 
		*/
		RouterHandler router = new RouterHandler();
		router.addMapping("/tokenizer/*", TokenizerController.class);
		router.addMapping("/extractor/keywords", KeywordsController.class);
		router.addMapping("/extractor/keyphrase", KeyphraseController.class);
		router.addMapping("/extractor/sentence", SentenceController.class);
		router.addMapping("/extractor/summary", SummaryController.class);
		server.setHandler(router);
		
		return this;
	}
	
	/**
	 * start the server 
	 * 
	 * @throws Exception 
	*/
	public void start() throws Exception
	{
		if ( server != null ) {
			server.start();
			server.join();
		}
	}
	
	/**
	 * stop the server 
	 * 
	 * @throws Exception 
	*/
	public void stop() throws Exception
	{
		if ( server != null ) {
			server.stop();
		}
	}

	public static void main(String[] args) 
	{
		JcsegServerConfig config = new JcsegServerConfig();
		JcsegServer server = new JcsegServer(config);
		
		try {
			server.registerHandler().start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
