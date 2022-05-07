package com.kisman.cc.util;

import com.kisman.cc.mixin.mixins.cubic.AccessorClassLoader;
import org.cubic.loader.Loader;
import org.cubic.loader.URLPath;
import org.cubic.loader.reflect.Instantiator;
import org.cubic.loader.urlpath.DefaultURLPath;
import org.luaj.vm2.ast.Str;
import org.lwjgl.opencl.CL;
import sun.security.krb5.internal.crypto.CksumType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.List;

public final class ReflectUtil extends SecurityManager {

    private ReflectUtil(){
    }

    private final static ReflectUtil instance;

    private static Class<?>[] stack;

    static {
        instance = new ReflectUtil();
        stack = instance.getClassContext();
    }

    public static Class<?> getCallerClass(){
        stack = instance.getClassContext();
        if(stack.length < 3)
            return null;
        return stack[2];
    }

    public static Class<?> getCallerClass(int depth){
        stack = instance.getClassContext();
        if(stack.length < depth + 1)
            return null;
        return stack[depth];
    }

    public static <T> T createInstance(Class<T> cls){
        return new Loader<T>(cls).loadFromClass(cls);
    }

    public static <T> T createInstance(Class<T> cls, Instantiator instantiator){
        Loader<T> loader = new Loader<>(cls);
        loader.instantiator(instantiator);
        return loader.loadFromClass(cls);
    }

    public static <T> List<T> load(Class<T> cls, String pkg){
        return new Loader<>(cls).loadFromPackage(pkg);
    }

    public static <T> List<T> load(Class<T> cls, String pkg, Instantiator instantiator){
        Loader<T> loader = new Loader<>(cls);
        loader.instantiator(instantiator);
        return loader.loadFromPackage(pkg);
    }

    public static <T> List<T> loadAll(Class<T> cls, String pkg){
        return new Loader<>(cls).loadAllFromPackage(pkg);
    }

    public static <T> List<T> loadAll(Class<T> cls, String pkg, Instantiator instantiator){
        Loader<T> loader = new Loader<>(cls);
        loader.instantiator(instantiator);
        return loader.loadAllFromPackage(pkg);
    }

    public static Object getObjectFieldVal(Class<?> cls, String name, Object instance){
        try {
            Field f = cls.getDeclaredField(name);
            f.setAccessible(true);
            return f.get(instance);
        } catch (Exception e){
            return null;
        }
    }

    public static Object getStaticFieldVal(Class<?> cls, String name){
        try {
            Field f = cls.getDeclaredField(name);
            f.setAccessible(true);
            return f.get(null);
        } catch (Exception e){
            return null;
        }
    }

    public static Object invoke(Class<?> cls, Object instance, String method, Class<?>[] paramTypes,Object... args){
        try {
            Method m = cls.getDeclaredMethod(method, paramTypes);
            m.setAccessible(true);
            return m.invoke(instance, args);
        } catch (Exception e){
            return null;
        }
    }

    public static Object invoke(Method method, Object instance, Object... args){
        try {
            method.setAccessible(true);
            return method.invoke(instance, args);
        } catch (Exception e){
            return null;
        }
    }

    public static Method getMethod(Class<?> cls, String methodName, Class<?>[] paramTypes){
        try {
            return cls.getDeclaredMethod(methodName, paramTypes);
        } catch (Exception e){
            return null;
        }
    }

    public static Package getPackage(ClassLoader cl, String pkg){
        return ((AccessorClassLoader) cl).getPackage(pkg);
    }

    public static Class<?> findLoadedClass(ClassLoader cl, String cls){
        return ((AccessorClassLoader) cl).findLoadedClass(cls);
    }

    public static Class<?> findClass(ClassLoader cl, String name) throws ClassNotFoundException {
        return ((AccessorClassLoader) cl).findClass(name);
    }

    public static Class<?> classForName(ClassLoader cl, String name, boolean initialize) throws ClassNotFoundException {
        return Class.forName(name, initialize, cl);
    }
}
