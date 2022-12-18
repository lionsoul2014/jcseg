package org.lionsoul.jcseg.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.lionsoul.jcseg.ISegment;
import org.lionsoul.jcseg.dic.ADictionary;
import org.lionsoul.jcseg.dic.DictionaryFactory;
import org.lionsoul.jcseg.json.JSONArray;
import org.lionsoul.jcseg.json.JSONException;
import org.lionsoul.jcseg.json.JSONObject;
import org.lionsoul.jcseg.segmenter.SegmenterConfig;
import org.lionsoul.jcseg.server.controller.MainController;
import org.lionsoul.jcseg.server.controller.KeyphraseController;
import org.lionsoul.jcseg.server.controller.KeywordsController;
import org.lionsoul.jcseg.server.controller.SentenceController;
import org.lionsoul.jcseg.server.controller.SummaryController;
import org.lionsoul.jcseg.server.controller.TokenizerController;
import org.lionsoul.jcseg.server.core.AbstractRouter;
import org.lionsoul.jcseg.server.core.DynamicRestRouter;
import org.lionsoul.jcseg.server.core.ServerConfig;
import org.lionsoul.jcseg.server.core.StandardHandler;
import org.lionsoul.jcseg.util.Util;

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
    private final ServerConfig config;
    
    /**
     * jetty server instance 
    */
    private Server server;
    
    /**
     * global resource pool 
    */
    private final JcsegGlobalResource resourcePool;
    
    /**
     * construct method
     * 
     * @param    config
    */
    public JcsegServer(ServerConfig config)
    {
        this.config = config;
        resourcePool = new JcsegGlobalResource();
    }
    
    /**
     * initialize the server and register the basic context handler
    */
    private void init()
    {
        //setup thread pool
        final QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMaxThreads(config.getMaxThreadPoolSize());
        threadPool.setIdleTimeout(config.getThreadIdleTimeout());
        
        server = new Server(threadPool);
        
        //setup http configuration
        final HttpConfiguration http_config = new HttpConfiguration();
        http_config.setOutputBufferSize(config.getOutputBufferSize());
        http_config.setRequestHeaderSize(config.getRequestHeaderSize());
        http_config.setResponseHeaderSize(config.getResponseHeaderSize());
        http_config.setSendServerVersion(false);
        http_config.setSendDateHeader(false);
        
        //setup connector
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
    public void registerHandler()
    {
        final String basePath = this.getClass().getPackage().getName()+".controller";
        final AbstractRouter router = new DynamicRestRouter(basePath, MainController.class);
        router.addMapping("/extractor/keywords", KeywordsController.class);
        router.addMapping("/extractor/keyphrase", KeyphraseController.class);
        router.addMapping("/extractor/sentence", SentenceController.class);
        router.addMapping("/extractor/summary", SummaryController.class);
        router.addMapping("/tokenizer/default", TokenizerController.class);
        
        /*
         * the rest of path and dynamic rest checking will handler it 
        */
        //router.addMapping("/tokenizer/default", TokenizerController.class);
        
        /*
         * prepare standard handler
        */
        final StandardHandler stdHandler = new StandardHandler(config, resourcePool, router);
        
        /*
         * prepare the resource handler 
        */
        final JcsegResourceHandler resourceHandler = new JcsegResourceHandler();
        
        /*
         * I am going to rewrite the path to handler mapping mechanism
         * check the Router handler for more info 
        */
        final GzipHandler gzipHandler = new GzipHandler();
        final HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{stdHandler, resourceHandler});
        gzipHandler.setHandler(handlers);
        server.setHandler(gzipHandler);

    }
    
    /**
     * load configuration.
     * 1. initialize the server config
     * 2. register global resource (global resource initialize)
     *
     * @param globalConfig
     */
    public void initFromGlobalConfig(JSONObject globalConfig) throws CloneNotSupportedException
    {
        /*
         * parse and initialize the server according to the global config
        */
        if ( globalConfig.has("server_config") ) {
            final JSONObject serverSetting = globalConfig.getJSONObject("server_config");
            if ( serverSetting.has("host") ) {
                config.setHost(serverSetting.getString("host"));
            }
            if ( serverSetting.has("port") ) {
                config.setPort(serverSetting.getInt("port"));
            }
            if ( serverSetting.has("charset") ) {
                config.setCharset(serverSetting.getString("charset")); 
            }
            if ( serverSetting.has("max_thread_pool_size") ) {
                config.setMaxThreadPoolSize(serverSetting.getInt("max_thread_pool_size"));
            }
            if ( serverSetting.has("thread_idle_timeout") ) {
                config.setThreadIdleTimeout(serverSetting.getInt("thread_idle_timeout"));
            }
            if ( serverSetting.has("http_output_buffer_size") ) {
                config.setOutputBufferSize(serverSetting.getInt("http_output_buffer_size"));
            }
            if ( serverSetting.has("http_request_header_size") ) {
                config.setRequestHeaderSize(serverSetting.getInt("http_request_header_size"));
            }
            if ( serverSetting.has("http_response_header_size") ) {
                config.setResponseHeaderSize(serverSetting.getInt("http_connection_idle_timeout"));
            }
        }

        //create a global JcsegTaskConfig and initialize from the global_setting
        final SegmenterConfig globalJcsegTaskConfig = new SegmenterConfig(false);
        if ( globalConfig.has("jcseg_global_config") ) {
            final JSONObject globalSetting = globalConfig.getJSONObject("jcseg_global_config");
            resetJcsegTaskConfig(globalJcsegTaskConfig, globalSetting);
        }
        
        /*
         * create the dictionaries according to the definition of dict.
         * and we will make a copy of the globalSetting for dictionary load
         * 
         * reset the max length to pass the dictionary words length limitation
        */
        final SegmenterConfig dictLoadConfig = globalJcsegTaskConfig.clone();
        dictLoadConfig.setMaxLength(100);
        if ( globalConfig.has("jcseg_dict") ) {
            final JSONObject dictSetting = globalConfig.getJSONObject("jcseg_dict");
            final String[] dictNames = JSONObject.getNames(dictSetting);
            if (dictNames != null) {
                for (String name : dictNames) {
                    final JSONObject dicJson = dictSetting.getJSONObject(name);
                    if (!dicJson.has("path")) {
                        throw new IllegalArgumentException("Missing path for dict instance " + name);
                    }

                    String[] lexPath = null;
                    if (!dicJson.isNull("path")) {
                        //process the lexPath
                        final JSONArray path = dicJson.getJSONArray("path");
                        final List<String> dicPath = new ArrayList<>();
                        for (int i = 0; i < path.length(); i++) {
                            String filePath = path.get(i).toString();
                            if (filePath.contains("{jar.dir}")) {
                                filePath = filePath.replace("{jar.dir}", Util.getJarHome(this));
                            }
                            dicPath.add(filePath);
                        }

                        lexPath = new String[dicPath.size()];
                        dicPath.toArray(lexPath);
                        dicPath.clear();
                    }

                    boolean loadPos = !dicJson.has("loadpos") || dicJson.getBoolean("loadpos");
                    boolean loadPinyin = !dicJson.has("loadpinyin") || dicJson.getBoolean("loadpinyin");
                    boolean loadSyn = !dicJson.has("loadsyn") || dicJson.getBoolean("loadsyn");
                    boolean loadEntity = !dicJson.has("loadentity") || dicJson.getBoolean("loadentity");
                    boolean autoload = dicJson.has("autoload") && dicJson.getBoolean("autoload");
                    int pollTime = dicJson.has("polltime") ? dicJson.getInt("polltime") : 300;

                    dictLoadConfig.setLoadCJKPinyin(loadPinyin);
                    dictLoadConfig.setLoadCJKPos(loadPos);
                    dictLoadConfig.setLoadCJKSyn(loadSyn);
                    dictLoadConfig.setLoadEntity(loadEntity);
                    dictLoadConfig.setAutoload(autoload);
                    dictLoadConfig.setPollTime(pollTime);
                    dictLoadConfig.setLexiconPath(lexPath);

                    //create and register the global dictionary resource
                    final ADictionary dic = DictionaryFactory.createDefaultDictionary(dictLoadConfig);
                    resourcePool.addDict(name, dic);
                }
            }
        }
        
        /*
         * create the JcsegTaskConfig instance according to the definition config
        */
        if ( globalConfig.has("jcseg_config") ) {
            final JSONObject configSetting = globalConfig.getJSONObject("jcseg_config");
            final String[] configNames = JSONObject.getNames(configSetting);
            if (configNames != null) {
                for (String name : configNames) {
                    final JSONObject configJson = configSetting.getJSONObject(name);

                    //clone the globalJcsegTaskConfig
                    //and do the override working by local defination
                    final SegmenterConfig config = globalJcsegTaskConfig.clone();
                    if (configJson.length() > 0) {
                        resetJcsegTaskConfig(config, configJson);
                    }

                    //register the global resource
                    resourcePool.addConfig(name, config);
                }
            }
        }
        
        /*
         * create the tokenizer instance according the definition of tokenizer
        */
        if ( globalConfig.has("jcseg_tokenizer") ) {
            final JSONObject tokenizerSetting = globalConfig.getJSONObject("jcseg_tokenizer");
            final String[] tokenizerNames = JSONObject.getNames(tokenizerSetting);
            if (tokenizerNames != null) {
                for (String name : tokenizerNames) {
                    final JSONObject tokenizerJson = tokenizerSetting.getJSONObject(name);

                    int algorithm = tokenizerJson.has("algorithm")
                            ? tokenizerJson.getInt("algorithm") : ISegment.COMPLEX_MODE;

                    if (!tokenizerJson.has("dict")) {
                        throw new IllegalArgumentException("Missing dict setting for tokenizer " + name);
                    }
                    if (!tokenizerJson.has("config")) {
                        throw new IllegalArgumentException("Missing config setting for tokenizer " + name);
                    }

                    final ADictionary dic = resourcePool.getDict(tokenizerJson.getString("dict"));
                    final SegmenterConfig config = resourcePool.getConfig(tokenizerJson.getString("config"));
                    if (dic == null) {
                        throw new IllegalArgumentException("Unknow dict instance "
                                + tokenizerJson.getString("dict") + " for tokenizer " + name);
                    }

                    if (config == null) {
                        throw new IllegalArgumentException("Unknow config instance "
                                + tokenizerJson.getString("config") + " for tokenizer " + name);
                    }

                    resourcePool.addTokenizerEntry(name, new JcsegTokenizerEntry(algorithm, config, dic));
                }
            }
        }

        //now, initialize the server
        init();
    }
    

    /**
     * reset a JcsegTaskConfig from a JSONObject
     * 
     * @param	config
     * @param   json
    */
    private void resetJcsegTaskConfig(SegmenterConfig config, JSONObject json)
    {
    	for ( final String key : json.keySet() ) {
    		try {
				config.set(key.replace('_', '.'), json.get(key).toString());
			} catch (JSONException | IOException e) {
				e.printStackTrace();
			}
        }
    }

    /**
     * start the server 
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
    */
    public void stop() throws Exception
    {
        if ( server != null ) {
            server.stop();
        }
    }

}
