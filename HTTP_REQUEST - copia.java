/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Date;

/**
 *
 * @author xabie
 */
public class HTTP_REQUEST {
    private PETITION petition;
    private HEADER header;    
    private Date received;
    private boolean timeout = false;
    
    public HTTP_REQUEST(String first, BufferedReader reader) {
        received = new Date();
        
        petition = new PETITION(first);
        header = new HEADER(reader);

    }
    
    public PETITION getPetition() {
        return petition;
    }
    
    public boolean timedOut() {
        return timeout;
    }
    
    public HEADER getHeader() {
        return header;
    }
    
    @Override
    public String toString() {
        String s = petition.getMethod().toString();
        
        StringBuilder sb = new StringBuilder(s);
        sb.append(" ").append(petition.getUrl()).append(" ").append(petition.getVersion().toString()).append("\n");
        sb.append(header.getHost()).append("\n");
        sb.append(header.getUser_agent()).append("\n");
        sb.append(header.getIf_modified_since().toString()).append("\n");
        return sb.toString();
    }
    
    public boolean isMalformed() {
        return (petition.isMalformed());
    }
    
    public Date getReceived() {
        return received;
    }
}
