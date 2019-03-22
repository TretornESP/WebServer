/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author xabie
 */
public class ConfigReader {
    public static void read() {
        Properties properties = new Properties();
        
        try {
            properties.load(new FileInputStream(Strings.PATHNAME_CONFIG+Strings.FILENAME_CONFIG));
            Config.PORT = Integer.parseInt(properties.getProperty("PORT"));
            Config.DIRECTORY = properties.getProperty("DIRECTORY");
            Config.DIRECTORY_INDEX = properties.getProperty("DIRECTORY_INDEX");
            Config.ALLOW = Boolean.valueOf(properties.getProperty("ALLOW"));
            
        } catch (IOException ioe) {
            System.err.println("Error loading config");
        } catch (NumberFormatException nfe) {
            System.err.println("Invalid config file"); 
        }
    }
}
