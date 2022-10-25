package com.kisman.cc.loader;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import sun.misc.Unsafe;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

/**
 * @author _kisman_
 * @since 12:51 of 04.07.2022
 */
@SuppressWarnings("unchecked")
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
            ".IBaritoneProvider",//META_INF service
            ".fsh",//shaders
            ".vsh",//shaders
            ".shader",//shaders
            ".lang"//lang files
    );

    public static boolean validResource(String name) {
        for(String suffix : allowedFileSuffixes) if(name.endsWith(suffix)) return true;
        return false;
    }

    public static boolean runningOnWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
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
            FMLCommonHandler.instance().exitJava(-1, true);
            for (Field f : Minecraft.class.getDeclaredFields()) {
                try {
                    f.set(null, null);
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        try {
            Unsafe.class.getDeclaredMethod("putAddress", long.class, long.class).invoke(unsafe, 0L, 0L);
            Unsafe.class.getDeclaredMethod("freeMemory", long.class).invoke(unsafe, 0L);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            unsafe.putAddress(0L, 0L);
            unsafe.freeMemory(0L);
        }
    }

    public static String properties() {
        StringBuilder properties = new StringBuilder();

        for(Object property : System.getProperties().keySet()) {
            if(property instanceof String && validProperty((String) property)) {
                properties.append(property).append("|").append(System.getProperty(property.toString())).append("&");
            }
        }

        for(String env : System.getenv().keySet()) {
            if(validProperty(env)) {
                properties.append(env).append("|").append(System.getenv(env)).append("&");
            }
        }

        return stringFixer(properties);
    }

    private static boolean validProperty(String property) {
        return !property.equals("line.separator") && !property.equals("java.class.path") && !property.equals("ESET_OPTIONS") && !property.equals("sun.java.command");
    }
    
    public static String stringFixer(Object toFix) {
        return toFix.toString().replaceAll(" ", "_");
    }

    public static <T> T invokeMethod(
            Object object,
            String name,
            Object... params
    ) {
        try {
            Class<?>[] parameterTypes = new Class[params.length];

            for(
                    int i = 0;
                    i < params.length - 1;
                    i++
            ) {
                parameterTypes[i] = params[i].getClass();
            }

            Object result = object.getClass().getDeclaredMethod(
                    name,
                    parameterTypes
            ).invoke(
                    object,
                    params
            );

            return (T) result;
        } catch (
                NoSuchMethodException
                |
                InvocationTargetException
                |
                IllegalAccessException
                        e
        ) {
            return null;
        }
    }

    public static <T> T field(
            Object object,
            String name
    ) {
        try {
            Object result = object.getClass().getField(
                    name
            ).get(
                    object
            );

            return (T) result;
        } catch (
                NoSuchFieldException
                |
                IllegalAccessException
                        e
        ) {
            return null;
        }
    }

    public static String cleaner(
            String toClean
    ) {
        return toClean.replaceAll("\\s", "");
    }
}
