package org.cubic.eventsystem;

import java.lang.reflect.Method;

class Listener {

    private final Method method;

    private final Class<?> declaringClass;

    private final Class<?> eventType;

    private final Object instance;

    private Listener(Method method, Object instance) {
        method.setAccessible(true);
        Class<?>[] paramTypes = method.getParameterTypes();
        //if(paramTypes.length != 1)
        //    throw new NotAListenerException();
        //if(method.getAnnotation(Subscribe.class) == null)
        //    throw new NotAListenerException();
        this.method = method;
        this.declaringClass = method.getDeclaringClass();
        this.eventType = paramTypes[0];
        this.instance = instance;
    }

    public static Listener newListener(MethodWrapper wrapper){
        Method m = wrapper.getMethod();
        Object instance = wrapper.getInstance();
        if(!isListener(m))
            return null;
        return new Listener(m, instance);
    }

    public void invoke(Object event){
        try {
            method.invoke(instance, event);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public Class<?> declaringClass(){
        return declaringClass;
    }

    public Class<?> eventType(){
        return eventType;
    }

    public Object getInstance(){
        return instance;
    }

    public static boolean isListener(Method m){
        Class<?>[] paramTypes = m.getParameterTypes();
        return paramTypes.length == 1 && m.getAnnotation(Subscribe.class) != null;
    }
}
