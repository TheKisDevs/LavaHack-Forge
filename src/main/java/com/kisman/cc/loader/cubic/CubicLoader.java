package com.kisman.cc.loader.cubic;

import com.kisman.cc.loader.cubic.CubicMap;
import com.kisman.cc.loader.antidump.AntiDump;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import sun.reflect.Reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Very important: Do not merge this class with Loader class
 * Trust me
 * @author Cubic
 */
public class CubicLoader {

    private static Map<String, byte[]> cache;

    static final Map<String, Supplier<byte[]>> map = new CubicMap<>();

    public static void init(){
        try {
            Field f = LaunchClassLoader.class.getDeclaredField("resourceCache");
            f.setAccessible(true);
            cache = (Map<String, byte[]>) f.get(Launch.classLoader);

            // make custom map to prevent our classes from getting cached
            Field f1 = LaunchClassLoader.class.getDeclaredField("cachedClasses");
            f1.setAccessible(false);
            ConcurrentHashMap<String, Class<?>> cachedClasses = (ConcurrentHashMap<String, java.lang.Class<?>>) f1.get(Launch.classLoader);
            f1.set(Launch.classLoader, new com.kisman.cc.loader.cubic.ClassCache(cachedClasses));

            // disable debugging
            setStaticFinalField(LaunchClassLoader.class.getDeclaredField("DEBUG_FINER"), false);
            setStaticFinalField(LaunchClassLoader.class.getDeclaredField("DEBUG_SAVE"), false);
        } catch (Exception e) {
        }
        Launch.classLoader.registerTransformer(com.kisman.cc.loader.cubic.ClassTransformer.class.getName());
    }

    private static void setStaticFinalField(Field field, Object value){
        try {
            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.set(field, field.getModifiers() & ~Modifier.FINAL);

            field.set(null, value);
        } catch (Exception ignored){
        }
    }

    public static void load(String className, byte[] bytes){
        cache.put(className, new byte[0]);
        map.put(className, () -> {
            if(Reflection.getCallerClass(3) != com.kisman.cc.loader.cubic.ClassTransformer.class){
                // some tries to dump us :(
                AntiDump.dumpDetected();
                return new byte[0];
            }
            return bytes;
        });
    }
}
