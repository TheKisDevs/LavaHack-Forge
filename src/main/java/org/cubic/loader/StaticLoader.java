package org.cubic.loader;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;

public final class StaticLoader {

    public static <T> List<T> loadFromPackage(Class<T> cls, String pkg){
        return new Loader<T>(cls).loadFromPackage(pkg);
    }

    public static <T> List<T> loadAllFromPackage(Class<T> cls, String pkg){
        return new Loader<T>(cls).loadAllFromPackage(pkg);
    }

    public static <E, T> void resolveFromPackageClasses(List<E> list, Class<T> cls, String pkg, Function<T, E> func){
        List<T> classes = new Loader<>(cls).loadFromPackage(pkg);
        for(T t : classes){
            list.add(func.apply(t));
        }
    }

    public static <E, T> void resolveAllFromPackageClasses(List<E> list, Class<T> cls, String pkg, Function<T, E> func){
        List<T> classes = new Loader<>(cls).loadAllFromPackage(pkg);
        for(T t : classes){
            list.add(func.apply(t));
        }
    }
}
