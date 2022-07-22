package com.kisman.cc.loader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author _kisman_
 * @since 12:51 of 04.07.2022
 */
public class Utility {
    public static List<String> allowedFileSuffixes = Arrays.asList(
            ".png",//images
            ".glsl",//shaders
            ".shader",//shaders
            ".frag",//shaders
            ".vert",//shaders
            ".jpg",//images
            ".ttf",//fonts
            ".json",//lang files, shaders
            ".csv",//plugin mappings
            ".ScriptEngineFactory",//META_INF service
            ".fsh",//shaders
            ".vsh"//shaders

    );

    public static boolean validResource(String name) {
        for(String suffix : allowedFileSuffixes) if(name.endsWith(suffix)) return true;
        return false;
    }

    public static boolean runningFromIntelliJ() {
        return System.getProperty("java.class.path").contains("idea_rt.jar");
    }

    public static byte[] getBytesFromInputStream(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[0xFFFF];
        for (int len = is.read(buffer); len != -1; len = is.read(buffer)) os.write(buffer, 0, len);
        return os.toByteArray();
    }
}
