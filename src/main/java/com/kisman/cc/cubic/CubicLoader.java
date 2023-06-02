package com.kisman.cc.cubic;

import com.kisman.cc.loader.antidump.AntiDump;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.commons.io.IOUtils;
import sun.reflect.Reflection;

import java.lang.reflect.Field;
import java.util.Map;
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
        } catch (Exception e) {
        }
        Launch.classLoader.registerTransformer(ClassTransformer.class.getName());
    }

    public static void load(String className, byte[] bytes){
        cache.put(className, new byte[0]);
        map.put(className, () -> {
            if(Reflection.getCallerClass(2) != ClassTransformer.class){
                AntiDump.dumpDetected();
                return null;
            }
            return bytes;
        });
    }
}
