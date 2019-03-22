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
public enum Versions {
    DEFAULT("HTTP/1.1"), ONE("HTTP/1.0"), ONE_ONE("HTTP/1.1"), TWO("HTTP/2"), UNSUPORTED("UNSUPORTED");
    
    private final String descr;
    private Versions(String descr) {
        this.descr = descr;
    }
    
    @Override
    public String toString() {
        return descr;
    }
}
