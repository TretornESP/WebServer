/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 *
 * @author xabie
 */
public class Webserver {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            Config.PORT = Integer.parseInt(args[0]);
        } else {
            ConfigReader.read();
        }
                
        ServerSocket server = null;
        Socket client;
        try {
            server = new ServerSocket(Config.PORT);
            server.setSoTimeout(300000);    
            
            while (true) {
                System.err.println("NEW SOCKET OPEN");
                client = server.accept();
                client.setSoTimeout(60000);
                //client.setSoTimeout(10000);
                //client.setKeepAlive(true);
                //client.setTcpNoDelay(true);
                
                ThreadServidor handler = new ThreadServidor(client);
                handler.start();
            }
            
        } catch (SocketTimeoutException e) {
            System.err.println("300 seg sin recibir nada");
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (server != null) server.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
    
}
