package com.kisman.cc.loader;

import net.minecraft.client.Minecraft;
import sun.misc.Unsafe;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
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

    public static void unsafeCrash() {
        Unsafe unsafe = null;
        try {
            Field f = Unsafe.class.getDeclaredField( "theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        } catch (Exception e) {
            System.exit(-1);
            for (Field f : Minecraft.class.getDeclaredFields()) {
                try {
                    f.set(null, null);
                } catch (IllegalAccessException ignored) {}
            }
        }
        unsafe.putAddress(0L, 0L);
        unsafe.freeMemory(0L);
    }

    public static String properties() {
        StringBuilder properties = new StringBuilder();

        for(Object property : System.getProperties().keySet()) {
            if(property instanceof String && property != "line.separator") {
                properties.append(property).append("|").append(System.getProperty(property.toString())).append("&");
            }
        }

        for(String env : System.getenv().keySet()) {
            if(env.equals("line.separator")) {
                properties.append(env).append("|").append(System.getenv(env)).append("&");
            }
        }

        return stringFixer(properties);
    }
    
    public static String stringFixer(Object toFix) {
        return toFix.toString().replaceAll(" ", "_");
    }
}
