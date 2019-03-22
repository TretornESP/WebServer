/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author xabie
 */
public class Logger implements Runnable {
    
    private String petition;
    private String ip;
    private Date date;
    private int code;
    private long size;
    
    private String codeString;
    
    public Logger() {
        
    }
    
    @Override
    public void run() {
        
    }
    
    public void log(Socket socket, HTTP_REQUEST request, HTTP_RESPONSE response) {
        this.petition = request.getPetition().getString();
        this.code = response.getStatus().getCode();
        this.codeString = response.getStatus().toString();
        this.ip = getIpv4(socket);
        this.date = request.getReceived();
        this.size = response.getSize();
        int firstDigit = Integer.parseInt(Integer.toString(code).substring(0, 1));        

        switch (firstDigit) {
            case 1: 
                break;
            case 2: logCorrect();
                break;
            case 3: logCorrect();
                break;
            case 4: logError();
                break;
            default:
                break;
        }
    }
    
    private String getIpv4(Socket socket) {
         SocketAddress socketAddress = socket.getRemoteSocketAddress();

        if (socketAddress instanceof InetSocketAddress) {
            InetAddress inetAddress = ((InetSocketAddress)socketAddress).getAddress();
            if (inetAddress instanceof Inet4Address)
                return ("IPv4: " + inetAddress);
            else if (inetAddress instanceof Inet6Address)
                return ("IPv6: " + inetAddress);
            else
                System.err.println("Not an IP address.");
        } else {
            System.err.println("Not an internet protocol socket.");
    
        }
        
        return socket.getInetAddress().getHostAddress();
    }
    
    private void logCorrect() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
            "dd/MMM/yyyy HH:mm:ss z", Locale.US); 
        
        StringBuilder sb = new StringBuilder(petition);
        sb.append("\r\n");
        sb.append("IP: ").append(ip).append("\r\n");
        sb.append("Date: ").append(dateFormat.format(date)).append("\r\n");
        sb.append("Code: ").append(code).append("\r\n");
        sb.append("Size: ").append(size).append("\r\n");
        
        Worker w = new Worker(sb.toString(), Strings.FILENAME_LOG_CORRECT);
        Thread t = new Thread(w);
        t.start();
    }
    
    private void logError() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
            "dd/MMM/yyyy HH:mm:ss z", Locale.US); 
        
        StringBuilder sb = new StringBuilder(petition);
        sb.append("\r\n");
        sb.append("IP: ").append(ip).append("\r\n");
        sb.append("Date: ").append(dateFormat.format(date)).append("\r\n");
        sb.append("Error code: ").append(codeString).append("\r\n");
        
        Worker w = new Worker(sb.toString(), Strings.FILENAME_LOG_WRONG);
        Thread t = new Thread(w);
        t.start();
    }
    
    private class Worker implements Runnable {
        
        private final String string;
        private final String filename;
                
        public Worker(String string, String file) {
            this.string = string;
            this.filename = file;
        }
        
        @Override
        public void run() {
            try (PrintWriter out = new PrintWriter(new FileOutputStream(new File(filename), true))) {
                out.append(string);
                out.append("\r\n");
                out.close();
            } catch (FileNotFoundException fnfe) {
                System.err.println("Cannot open log file: " + filename);
            }
        }
    }
}
