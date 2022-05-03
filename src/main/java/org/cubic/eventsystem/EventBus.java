package org.cubic.eventsystem;

public interface EventBus {

    void subscribe(Class<?> cls);

    void subscribe(Object o);

    void unsubscribe(Class<?> cls);

    void unsubscribe(Object o);

    void post(Object event);

    static EventBus getDefault(){
        return new EventManager();
    }
}
