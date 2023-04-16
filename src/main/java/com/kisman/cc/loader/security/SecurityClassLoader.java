package com.kisman.cc.loader.security;

import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import sun.reflect.Reflection;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SecurityClassLoader extends LaunchClassLoader {

    public static SecurityClassLoader setClassLoader(LaunchClassLoader classLoader){
        SecurityClassLoader securityClassLoader = new SecurityClassLoader(classLoader);
        Launch.classLoader = securityClassLoader;
        Minecraft.getMinecraft().mcThread.setContextClassLoader(securityClassLoader);
        return securityClassLoader;
    }

    public static final Map<String, byte[]> RESOURCE_CACHE = new SecureMap<>(new ConcurrentHashMap<>());

    private final LaunchClassLoader classLoader;

    public SecurityClassLoader(LaunchClassLoader classLoader){
        super(classLoader.getURLs());
        this.classLoader = classLoader;
        if(RESOURCE_CACHE.getClass() != SecureMap.class)
            throw new SecurityException();
    }

    @Override
    public byte[] getClassBytes(String name) throws IOException {
        byte[] bytes = RESOURCE_CACHE.get(name);
        if(bytes != null){
            if(Reflection.getCallerClass(2) != LaunchClassLoader.class)
                throw new SecurityException();
            return bytes;
        }
        return classLoader.getClassBytes(name);
    }

    @Override
    public void registerTransformer(String transformerClassName) {
        classLoader.registerTransformer(transformerClassName);
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return classLoader.findClass(name);
    }

    @Override
    public void addURL(URL url) {
        classLoader.addURL(url);
    }

    @Override
    public List<URL> getSources() {
        return classLoader.getSources();
    }

    @Override
    public List<IClassTransformer> getTransformers() {
        return classLoader.getTransformers();
    }

    @Override
    public void addClassLoaderExclusion(String toExclude) {
        classLoader. addClassLoaderExclusion(toExclude);
    }

    @Override
    public void addTransformerExclusion(String toExclude) {
        classLoader.addTransformerExclusion(toExclude);
    }

    @Override
    public void clearNegativeEntries(Set<String> entriesToClear) {
        classLoader.clearNegativeEntries(entriesToClear);
    }
}
