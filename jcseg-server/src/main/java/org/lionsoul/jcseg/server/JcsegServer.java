package org.lionsoul.jcseg.server;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.lionsoul.jcseg.server.controller.MainController;
import org.lionsoul.jcseg.server.controller.KeyphraseController;
import org.lionsoul.jcseg.server.controller.KeywordsController;
import org.lionsoul.jcseg.server.controller.SentenceController;
import org.lionsoul.jcseg.server.controller.SummaryController;
import org.lionsoul.jcseg.server.core.AbstractRouter;
import org.lionsoul.jcseg.server.core.DynamicRestRouter;
import org.lionsoul.jcseg.server.core.StandardHandler;

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
	 * global resource pool 
	*/
	private GlobalResourcePool resourcePool = null;
	
	/**
	 * construct method
	 * 
	 * @param	config
	*/
	public JcsegServer(JcsegServerConfig config)
	{
		this.config = config;
		resourcePool = new GlobalResourcePool();
		init();
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
		String basePath = this.getClass().getPackage().getName()+".controller";
		AbstractRouter router = new DynamicRestRouter(basePath, MainController.class);
		router.addMapping("/extractor/keywords", KeywordsController.class);
		router.addMapping("/extractor/keyphrase", KeyphraseController.class);
		router.addMapping("/extractor/sentence", SentenceController.class);
		router.addMapping("/extractor/summary", SummaryController.class);
		
		/*
		 * the rest of path and dynamic rest checking will handler it 
		*/
		//router.addMapping("/tokenizer/default", TokenizerController.class);
		
		/*
		 * yet, i am going to rewrite the path to handler mapping mechanism
		 * check the Router handler for more info 
		*/
		server.setHandler(new StandardHandler(resourcePool, router));
		
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
