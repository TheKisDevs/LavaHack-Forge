package org.cubic.m2l;

import me.zero.alpine.bus.EventBus;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;

public class M2L {

    private final List<MethodToListener> listeners;

    private final EventBus eventBus;

    public M2L(EventBus eventBus){
        listeners = new Vector<>();
        this.eventBus = eventBus;
    }

    public void register(Object o){
        for(Method method : o.getClass().getDeclaredMethods()){
            M2LTarget m2LTarget = method.getAnnotation(M2LTarget.class);
            if(m2LTarget == null)
                continue;
            MethodToListener methodToListener = new MethodToListener(o, method, m2LTarget, method.getParameterTypes()[0]);
            listeners.add(methodToListener);
            eventBus.subscribe(methodToListener);
        }
    }

    public List<MethodToListener> getListeners(){
        return listeners;
    }

    public EventBus getEventBus(){
        return eventBus;
    }
}
