package org.lionsoul.jcseg.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.lionsoul.jcseg.json.JSONObject;
import org.lionsoul.jcseg.server.core.ServerConfig;
import org.lionsoul.jcseg.util.IStringBuffer;

public class JcsegServerConfig extends ServerConfig 
{
    /**
     * private jcseg server configuration file path 
    */
    private String configFile = null;
    
    /**
     * jcseg global setting JSONObject 
    */
    private JSONObject globalConfig = null;
    
    /**
     * construct method 
     * 
     * @param    configFile
     * @throws IOException 
    */
    public JcsegServerConfig(String configFile) throws IOException
    {
        if ( configFile != null ) {
            this.configFile = configFile;
            resetFromFile(configFile);
        }
    }
    
    public JcsegServerConfig()
    {
    }
    
    /**
     * initialize it from the specifield config file
     * 
     * @param    configFile
     * @throws IOException 
    */
    public void resetFromFile(String configFile) throws IOException
    {
        IStringBuffer isb = new IStringBuffer();
        String line = null;
        BufferedReader reader = new BufferedReader(new FileReader(configFile));
        while ( (line = reader.readLine()) != null ) {
            line = line.trim();
            if (line.equals("")) continue;
            if (line.charAt(0) == '#') continue;
            
            isb.append(line).append('\n');
            line = null;    //let gc do its work
        }
        
        globalConfig = new JSONObject(isb.toString());
        
        //let gc do its work
        isb = null;
        reader.close();
        reader = null;
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public JSONObject getGlobalConfig() {
        return globalConfig;
    }

    public void setGlobalConfig(JSONObject globalConfig) {
        this.globalConfig = globalConfig;
    }

}
