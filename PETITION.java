/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

import java.io.File;
import java.util.StringTokenizer;

/**
 *
 * @author xabie
 */
public class PETITION {
    private String string = "MALFORMED";
    private Methods method = Methods.UNSUPORTED;
    private String url  = "MALFORMED";
    private Versions version = Versions.UNSUPORTED;
    private boolean list = false;
    private boolean nulled = true;
    private boolean forbidden = false;
    
    public PETITION(String string) {
        if (string  == null) return;
        nulled = false;
        this.string = string;
        
        //System.out.println(string);
        StringTokenizer tokenizer = new StringTokenizer(string);
        if (!tokenizer.hasMoreTokens()) return;

        switch (tokenizer.nextToken()) {
            case "GET":
                method = Methods.GET;
                break;
            case "POST":
                method = Methods.POST;
                break;
            case "HEAD":
                method = Methods.HEAD;
                break;
            default:
                method = Methods.UNSUPORTED;
        }
        
        if (!tokenizer.hasMoreTokens()) return;
        url = tokenizer.nextToken();
        if (url.endsWith("/")) url = askfordir(url);
        
        if (!tokenizer.hasMoreTokens()) return;
        switch (tokenizer.nextToken()) {
            case "HTTP/1.0":
                version = Versions.ONE;
                break;
            case "HTTP/1.1":
                version = Versions.ONE_ONE;
                break;
            case "HTTP/2.0":
                version = Versions.TWO;
            default:
                version = Versions.UNSUPORTED;
        }
    }
    
    private String askfordir(String url) {
        String filename = Config.DIRECTORY+url.substring(1).replace("/", "\\")+Config.DIRECTORY_INDEX;
        File file = new File(filename);
        File folder = new File(Config.DIRECTORY+url.substring(1).replace("/", "\\"));
        //System.out.println("HEY!: "+filename);
        if (!file.exists() && folder.exists() && folder.isDirectory()) {
            System.out.println("DIR_INDEX = " + Config.DIRECTORY_INDEX);
            if (Config.ALLOW) {
                //System.err.println("WERE LSITING!!!!");
                list = true;
            } else {
                forbidden = true;
            }
        } else {
            return url+Config.DIRECTORY_INDEX;
        }
        return Config.DIRECTORY+url.substring(1).replace("/", "\\");
    }
    
    public boolean isNulled() {
        return nulled;
    }
    public boolean isMalformed() {
        return (string.equals("MALFORMED") || url.equals("MALFORMED") || method == Methods.UNSUPORTED ||version == Versions.UNSUPORTED);
    }
    
    public String getString() {
        return string;
    }
    
    public Methods getMethod() {
        return method;
    }
    
    public Versions getVersion() {
        return version;
    }
    
    public String getUrl() {
        return url;
    }
    
    public boolean isForbidden() {
        return forbidden;
    }
    
    public boolean list() {
        return list;
    }
}
