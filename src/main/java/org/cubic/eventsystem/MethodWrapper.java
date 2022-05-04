package org.cubic.eventsystem;

import java.lang.reflect.Method;

class MethodWrapper {

    private final Method method;

    private final Object instance;

    public MethodWrapper(Method method, Object instance){
        this.method = method;
        this.instance = instance;
    }

    public Method getMethod(){
        return method;
    }

    public Object getInstance(){
        return instance;
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof MethodWrapper))
            return false;
        MethodWrapper wrapper = (MethodWrapper) o;
        return method.equals(wrapper.method) && instance.equals(wrapper.instance);
    }
}
