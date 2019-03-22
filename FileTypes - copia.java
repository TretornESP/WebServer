/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

/**
 *
 * @author xabie
 */
public enum FileTypes {
    HTML("text/html"), PLAIN("text/plain"), GIF("image/gif"), PNG("image/png"), OCTET("application/octet-stream"), ICON("image/x-icon"), JPG("image/jpeg"), DO("application/x-do"); 
    
    private final String descr;
    private FileTypes(String descr) {
        this.descr = descr;
    }
    
    @Override
    public String toString() {
        return descr;
    }
}
