/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author xabie
 */
public class HTTP_RESPONSE {
    
    private boolean ready = false;
    
    private String string;
    private FileInputStream content;
    
    private Versions version;
    private Status status;
    private Date current_date;
    private String server;
    private Date last_modified;
    private long size;
    private FileTypes content_type;
    
    private boolean listing;
    private boolean dynamic;
    
    private File resource;
    private String resname;
    
    public HTTP_RESPONSE(HTTP_REQUEST request, String resource) {
        this.resource = new File(Config.DIRECTORY+resource);
        this.resname = resource;
        last_modified = new Date(this.resource.lastModified());      

        
        version = request.getPetition().getVersion();
        setStatus(request);
        
        if (status != Status.OK) {
            content_type = FileTypes.HTML;
        }

        if (request.getPetition().list()) {
            listing = true;
            Listing l = new Listing();
            l.load(resource);
            list(l);
            return;
        }
        listing = false;
        
       // System.out.println("SINCE: "+request.getHeader().getIf_modified_since() + " LAST: " + last_modified);
        
        if (status == Status.OK) setContentType();
        if (content_type == FileTypes.DO) {
            servlet();
            dynamic = true;
            return;
        }
        dynamic = false;
        
        parse();
        setResponse(request);
        ready = true;
    }
    
    private void servlet() {
        Map<String, String> parameters = new HashMap<>();
        System.out.println("TODOOOOOOOOOOOOOOOOOO");
        
        status = Status.OK;
        server = "XABI'S SERVER";
        last_modified = new Date();
        current_date = last_modified;
        content_type = FileTypes.HTML;
        
        int index = resname.lastIndexOf("?");
        String query;
        String result = Templates.BadRequest.getHtml();
      
        if (index > 0) {
            query = resname.substring(index + 1 );
        
            String[] params = query.split("&");
            for (String param : params) {
                String name = param.split("=")[0];
                String value = param.split("=")[1];
                System.out.println("ADDING: "+name+ ", "+value);
                parameters.put(name, value);
            }
        }
       
        try {
            result = Servlet.ServerUtils.processDynRequest("Servlet.MiServlet",parameters);
            size = result.getBytes().length;
        } catch (Exception ex) {
            System.out.println("EXCEPTION");
        }
       
        SimpleDateFormat dateFormat = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);  
       
        string = version.toString();
        
        StringBuilder sb = new StringBuilder(string);
        
        sb.append(" ").append(status.toString());
        sb.append("\r\n");
        sb.append("Date: ").append(dateFormat.format(current_date));
        sb.append("\r\n");
        sb.append("Server: ").append(server);
        sb.append("\r\n");
        sb.append("Last-Modified: ").append(dateFormat.format(last_modified));
        sb.append("\r\n");
        sb.append("Content-Length: ").append(size);
        sb.append("\r\n");
        sb.append("Content-Type: ").append(content_type);
        sb.append("\r\n");
        sb.append("\r\n");    
        sb.append(result);
        
        string = sb.toString();
        
       
    }
    
    private void list(Listing l) {
        System.out.println("LISTING");
        status = Status.OK;
        last_modified = new Date();
        current_date = last_modified;
        server = "XABI'S SERVER";
        size = l.getHTML().length();
        content_type = FileTypes.HTML;
        
        SimpleDateFormat dateFormat = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);  
       
        string = version.toString();
        
        StringBuilder sb = new StringBuilder(string);
        
        sb.append(" ").append(status.toString());
        sb.append("\r\n");
        sb.append("Date: ").append(dateFormat.format(current_date));
        sb.append("\r\n");
        sb.append("Server: ").append(server);
        sb.append("\r\n");
        sb.append("Last-Modified: ").append(dateFormat.format(last_modified));
        sb.append("\r\n");
        sb.append("Content-Length: ").append(size);
        sb.append("\r\n");
        sb.append("Content-Type: ").append(content_type);
        sb.append("\r\n");
        sb.append("\r\n");    
        sb.append(l.getHTML());
        
        string = sb.toString();
    }
    
    private void setStatus(HTTP_REQUEST request) {
        if (request.getPetition().isForbidden()) {
            status = Status.FORBIDDEN;
            return;
        } 
        
        if (request.isMalformed()) {
            status = Status.BAD_REQUEST;
            return;
        }
        if (resource.exists() && !resource.isDirectory()) {
            if (last_modified.after(request.getHeader().getIf_modified_since())) {
                status = Status.OK;
            } else {
                status = Status.NOT_MODIFIED;
            }
        } else {
            status = Status.NOT_FOUND;
        }
    }
    
    private void parse() {
        switch (status) {
            case OK: size = resource.length();
            break;
            case BAD_REQUEST: size = Templates.BadRequest.getHtml().length();
            break;
            case FORBIDDEN: size = Templates.Forbidden.getHtml().length();
            break;
            case NOT_FOUND: size = Templates.NotFound.getHtml().length();
            break;
            case NOT_MODIFIED: size = resource.length();
        }
        current_date = new Date();
        server = "XABI'S SERVER";
    }
    
    private void setContentType() {
       String extension;
              
       int index = resname.lastIndexOf(".");
      
       if (index > 0) {
           extension = resname.substring(index + 1 );
           int end = extension.indexOf("?");
           if (end > 0)
               extension = extension.substring(0, end);
       } else {
           extension = "unknown";
       }
       switch (extension) {
           case "html":
               content_type = FileTypes.HTML;
               break;
           case "txt": 
               content_type = FileTypes.PLAIN;
               break;
           case "gif":
               content_type = FileTypes.GIF;
               break;
           case "png":
               content_type = FileTypes.PNG;
               break;
           case "jpg":
               content_type = FileTypes.JPG;
               break;
           case "ico":
               content_type = FileTypes.ICON;
               break;
           case "do":
               content_type = FileTypes.DO;
               break;
           default:
               content_type = FileTypes.OCTET;
       }
    }
    
    @Override
    public String toString() {
        String res = version.toString();
        StringBuilder sb = new StringBuilder(res);
        SimpleDateFormat dateFormat = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);      
        
        sb.append(status.toString());
        sb.append(dateFormat.format(current_date));
        sb.append(server);
        sb.append(dateFormat.format(last_modified));
        sb.append(size);
        sb.append(content_type);
        
        return sb.toString();
    }
    
    private void setResponse(HTTP_REQUEST request) {
        
        SimpleDateFormat dateFormat = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss z", Locale.US); 
        string = version.toString();
        StringBuilder sb = new StringBuilder(string);
        
        sb.append(" ").append(status.toString());
        sb.append("\r\n");
        sb.append("Date: ").append(dateFormat.format(current_date));
        sb.append("\r\n");
        sb.append("Server: ").append(server);
        sb.append("\r\n");
        if (status == Status.NOT_MODIFIED) {sb.append("\r\n"); string = sb.toString(); return;}
        sb.append("Last-Modified: ").append(dateFormat.format(last_modified));
        sb.append("\r\n");
        sb.append("Content-Length: ").append(size);
        sb.append("\r\n");
        sb.append("Content-Type: ").append(content_type);
        sb.append("\r\n");
        sb.append("\r\n");
        
        if (request.getPetition().getMethod() == Methods.HEAD) {
            string = sb.toString();
            return;            
        }
        
        switch (status) {
            case OK:
            break;
            case BAD_REQUEST: sb.append(Templates.BadRequest.getHtml());
            break;
            case FORBIDDEN: sb.append(Templates.Forbidden.getHtml());
            break;
            case NOT_FOUND: sb.append(Templates.NotFound.getHtml());
            break;
        }                
        string = sb.toString();        
    }
    
    public String getResponse() {
        return string;
    }
    
    public int sendResponse(DataOutputStream writer) {
                
        try {
            byte[] buffer = getResponse().getBytes();
            writer.write(buffer);
                        
            //System.out.println("SENDING");
            //writer.writeBytes(getResponse());
            if (status != Status.OK || listing || dynamic) {return 0;}
            FileInputStream input = new FileInputStream(resource);
            int count;
            buffer = new byte[(int)size]; // or 4096, or more
            while ((count = input.read(buffer)) > 0){
                writer.write(buffer, 0, count);
            }
            return 0;
        } catch (SocketException se) {
            System.err.println("Client closed connection during write");
            //try {writer.flush();} catch (IOException ioe) {}
            se.printStackTrace();
            return -1;
        } catch (IOException ioe) {
            System.err.println("Error sending content");
            ioe.printStackTrace();
            return -1;
        }
    }
    
    public boolean isReady() {
        return ready;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public long getSize() {
        return size;
    }
}
