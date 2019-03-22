/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ThreadServidor extends Thread {
    private Logger logger;
    private Socket socket;
    
    public ThreadServidor(Socket s) {
        socket = s;
    }
    
    @Override
    public void run() {
        
        BufferedReader reader = null;
        DataOutputStream writer = null;
        logger = new Logger();
        
      //  System.out.println("HEY IM IN: " +new File(".").getAbsolutePath());
        try {
            while (true) {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new DataOutputStream(socket.getOutputStream());
                
                while (!reader.ready()) {Thread.sleep(100);}
                
                HTTP_REQUEST request = new HTTP_REQUEST(reader.readLine(), reader);
                if (request.timedOut() || request.getPetition().isNulled()) {System.out.println("ALGO PASA"); break;}
                HTTP_RESPONSE response = new HTTP_RESPONSE(request, request.getPetition().getUrl());

                logger.log(socket, request, response);
                if (response.sendResponse(writer)==-1) {System.out.println("responser break"); break;}
                writer.flush();
                System.out.println("-----------------------------");
                System.out.println(request.toString());
                System.out.println("*****************************");
                System.out.println(response.getResponse());
                System.out.println("-----------------------------");
                
                if (request.getPetition().getVersion() == Versions.ONE) break;
            }
            
        } catch (SocketTimeoutException e) {
            System.err.println("10 seg sin recibir nada");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } finally {
            try {
                if (socket != null) {System.err.println("SOCKET CLOSED"); socket.close();}
                if (reader != null) reader.close();
                if (writer != null) writer.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }    
}
