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
public enum Status {
    OK("200 OK", 200), BAD_REQUEST("400 Bad Request", 400), FORBIDDEN("403 Forbidden", 403), NOT_FOUND("404 Not Found", 404), NOT_MODIFIED("304 Not Modified", 304);
    
    private final String descr;
    private final int code;
    private Status(String descr, int code) {
        this.descr = descr;
        this.code = code;
    }
    
    @Override
    public String toString() {
        return descr;
    }

    public int getCode() {
        return code;
    }
}
