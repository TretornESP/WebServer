/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 *
 * @author xabie
 */
public class HEADER {
    private String host = "";
    private String user_agent = "";
    private Date if_modified_since = new Date(0);
    
    public HEADER(BufferedReader reader) {
        String line;
        
        try {
            while ((line = reader.readLine()) != null) {
                tokenize(line);
                if (line.isEmpty()) break;
            }    
        } catch (IOException ioe) {
            System.err.println("ERROR COMPOSING HEADER");
            ioe.printStackTrace();
        }
    }
  
    private void tokenize(String line) {
       // System.out.println("PARSING: " + line);
        StringTokenizer tokenizer = new StringTokenizer(line);
        if (tokenizer.countTokens() <= 1) return;
        String action = tokenizer.nextToken();
        
        StringBuilder sb = new StringBuilder();
        while (tokenizer.hasMoreTokens()) {
            sb.append(tokenizer.nextToken()).append(" ");
        }
        
        switch (action) {
            case "Host:":
                host = sb.toString();
            break;
            case "User-Agent:":
                user_agent = sb.toString();
            break;
            case "If-Modified-Since:":
                //System.out.println("HEYY: " + sb.toString());
                parseDate(sb.toString());
            break;
        }
    }
    
    private void parseDate(String text) {
         SimpleDateFormat dateFormat = new SimpleDateFormat(
        "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
         try {
            if_modified_since = dateFormat.parse(text);
         } catch (ParseException pe) {
             System.err.println("Couldn't parse date format");
             pe.printStackTrace();
         }
    }
    
    public boolean isMalformed() {
        return (host.equals("MALFORMED") || user_agent.equals("MALFORMED"));
    }
    
    public String getHost() {
        return host;
    }
    
    public Date getIf_modified_since() {
        return if_modified_since;
    }
    
    public String getUser_agent() {
        return user_agent;
    }
} 
