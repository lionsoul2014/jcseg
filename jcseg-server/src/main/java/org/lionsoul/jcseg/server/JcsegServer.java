package org.lionsoul.jcseg.server;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

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
		//common handler
		ContextHandler commonContext = new ContextHandler("/");
		commonContext.setHandler(new ErrorHandler());
		
		//tokenizer handler
		ContextHandler tokenizeContext = new ContextHandler("/tokenizer/");
		tokenizeContext.setHandler(new TokenizerHandler());
		
		//keywords extractor handler
		ContextHandler keywordsContext = new ContextHandler("/extractor/keywords/");
		keywordsContext.setHandler(new KeywordsExtractorHandler());
		
		//keyphrase extractor handler
		ContextHandler keyphraseContext = new ContextHandler("/extractor/keyphrase/");
		keyphraseContext.setHandler(new KeyphraseExtractorHandler());
		
		//key sentence extractor handler
		ContextHandler sentenceContext = new ContextHandler("/extractor/sentence/");
		sentenceContext.setHandler(new SentenceExtractorHandler());
		
		//summary extracotr handler
		ContextHandler summaryContext = new ContextHandler("/extractor/summary/");
		summaryContext.setHandler(new SummaryExtractorHandler());
		
		
		 * build a context handler collections
		 * and set it as the server's default context handler. 
		
		ContextHandlerCollection contexts = new ContextHandlerCollection();
		contexts.setHandlers(new Handler[]{
			commonContext, tokenizeContext, keywordsContext,
			keyphraseContext, sentenceContext, summaryContext
		});
		
		server.setHandler(contexts);*/
		
		/*
		 * yet, i am going to rewrite the path to handler mapping mechanism
		 * check the Router handler for more info 
		*/
		server.setHandler(new RouterHandler());
		
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
