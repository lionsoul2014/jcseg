package org.lionsoul.jcseg.server;

import java.io.File;

import org.lionsoul.jcseg.server.util.JettyEmptyLogger;
import org.lionsoul.jcseg.util.Util;

/**
 * Jcseg server bootstrap script
 * 
 * @author lionsoul<chenxin619315@gmail.com>
*/
public class Bootstrap
{


    public static void main(String[] args) 
    {
        final JcsegServerConfig config = new JcsegServerConfig();
        
        /*
         * get the jcseg-server.properties from the command line 
        */
        String proFile = null;
        if ( args.length > 0 ) {
            proFile = args[0];
        }
        
        /*
         * not specified, check the properties file in the jar dir 
        */
        if ( proFile == null ) {
            String[] rPaths = {"jcseg-server.properties", "classes/jcseg-server.properties"};
            String jarHome = Util.getJarHome(config);
            for ( String path : rPaths ) {
                File pFile = new File(jarHome + "/" + path);
                if ( pFile.exists() ) {
                    proFile = pFile.getAbsolutePath();
                    break;
                }
            }
        }
        
        //still not found, print an error and stop it right here
        if ( proFile == null ) {
            System.out.println("Usage: java -jar jcseg-server-{version}.jar "
                    + "\"path of file jcseg-server properties\"");
            return;
        }
        
        // disable the logging
        org.eclipse.jetty.util.log.Log.setLog(new JettyEmptyLogger());
        
        try {
            System.out.println("+-Try to load and parse server property file \"" + proFile + "\"");
            config.resetFromFile(proFile);
            JcsegServer server = new JcsegServer(config);
            System.out.print("+-[Info]: initializing ... ");
            server.initFromGlobalConfig(config.getGlobalConfig());
            System.out.println(" --[Ok]");
            System.out.print("+-[Info]: Register handler ... ");
            server.registerHandler();
            System.out.println(" --[Ok]");
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
