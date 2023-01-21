package com.kisman.cc.loader.security;

import sun.reflect.Reflection;

public class ClassContext {

    public static Class<?> getCallerClass(int depth){
        return Reflection.getCallerClass(depth);
    }
}
