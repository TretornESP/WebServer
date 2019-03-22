/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author xabie
 */
public class Listing {
    
    private String contents = "<h1>DIRECTORY LISTING OF ";
    private boolean ready = false;

    public void load(String directory) {
        File file = new File(directory);
        if (file.exists() && file.isDirectory()) parse(file);
    }
    
    private void parse(File file) {
        StringBuilder sb = new StringBuilder(contents);
        sb.append(file.getName()).append("</h1>\r\n\r\n\r\n");
        sb.append("<p><a href=\"..\">..</a>");

        File[] list = file.listFiles();
        
        for (File f: list) {
            String name = f.getName();
            if (f.isDirectory()) name = name+"/";
            sb.append("<p><a href=\"").append(name).append("\">").append(f.getName()).append("</a>");
        }
        
        contents = sb.toString();
        ready = true;
    }
    
    public static String getParentDirPath(String fileOrDirPath) {
        boolean endsWithSlash = fileOrDirPath.endsWith(File.separator);
        return fileOrDirPath.substring(0, fileOrDirPath.lastIndexOf(File.separatorChar, 
                endsWithSlash ? fileOrDirPath.length() - 2 : fileOrDirPath.length() - 1));
    }
    
    public String getHTML() {
        return contents;
    }
}
