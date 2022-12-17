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
     * initialize it from the specified config file
     * 
     * @param   configFile
    */
    public void resetFromFile(String configFile) throws IOException
    {
        final IStringBuffer isb = new IStringBuffer();
        final BufferedReader reader = new BufferedReader(new FileReader(configFile));
        String line = null;
        while ( (line = reader.readLine()) != null ) {
            line = line.trim();
            if (line.equals("")) continue;
            if (line.charAt(0) == '#') continue;
            
            isb.append(line).append('\n');
            line = null;    //let gc do its work
        }
        
        globalConfig = new JSONObject(isb.toString());
        
        //let gc do its work
        reader.close();
    }

    public String getConfigFile()
    {
        return configFile;
    }

    public void setConfigFile(String configFile)
    {
        this.configFile = configFile;
    }

    public JSONObject getGlobalConfig()
    {
        return globalConfig;
    }

    public void setGlobalConfig(JSONObject globalConfig)
    {
        this.globalConfig = globalConfig;
    }

}
