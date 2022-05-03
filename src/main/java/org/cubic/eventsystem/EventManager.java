package org.cubic.eventsystem;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class EventManager implements EventBus {

    private final Map<Class<?>, List<Listener>> LISTENER;

    private final Map<MethodWrapper, Listener> CACHE;

    public EventManager(){
        LISTENER = new ConcurrentHashMap<>(128);
        CACHE = new ConcurrentHashMap<>(256);
    }


    @Override
    public void subscribe(Class<?> cls) {
        subscribe(cls, null);
    }

    @Override
    public void subscribe(Object o) {
        subscribe(o.getClass(), o);
    }

    private void subscribe(Class<?> cls, Object instance){
        for(Method m : cls.getDeclaredMethods()){
            MethodWrapper wrapper = new MethodWrapper(m, instance);
            Listener listener = Listener.newListener(wrapper);
            if(listener == null)
                continue;
            List<Listener> listeners = LISTENER.computeIfAbsent(listener.eventType(), t -> new ArrayList<>(64));
            listeners.add(listener);
            CACHE.put(wrapper, listener);
        }
    }

    @Override
    public void unsubscribe(Class<?> cls) {
        unsubscribe(cls, null);
    }

    @Override
    public void unsubscribe(Object o) {
        unsubscribe(o.getClass(), o);
    }

    private void unsubscribe(Class<?> cls, Object instance){
        for(Method m : cls.getDeclaredMethods()){
            MethodWrapper wrapper = new MethodWrapper(m, instance);
            Listener listener = CACHE.get(wrapper);
            if(listener == null)
                continue;
            List<Listener> listeners = LISTENER.computeIfAbsent(listener.eventType(), t -> new ArrayList<>(64));
            listeners.remove(listener);
            CACHE.remove(wrapper);
        }
    }

    @Override
    public void post(Object event) {
        List<Listener> listeners = LISTENER.get(event.getClass());
        if(listeners == null)
            return;
        for(Listener listener : listeners){
            listener.invoke(event);
        }
    }
}
