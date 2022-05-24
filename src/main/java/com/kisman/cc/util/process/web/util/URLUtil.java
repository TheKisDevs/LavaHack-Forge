package com.kisman.cc.util.process.web.util;

import java.io.File;
import java.net.URI;
import java.net.URL;

public class URLUtil {

    public static URL parseURL(URI uri){
        try {
            return uri.toURL();
        } catch (Exception e){
            return null;
        }
    }

    public static URL parseURL(File file){
        File f = file.getAbsoluteFile();
        URI uri = f.toURI();
        try {
            return uri.toURL();
        } catch (Exception e){
            return null;
        }
    }

    public static URL parseURL(String path){
        return parseURL(new File(path));
    }

    public static URL parseURL(Class<?> cls){
        ClassLoader cl = cls.getClassLoader();
        return cl.getResource(cls.getName().replace('.', '/'));
    }
}
