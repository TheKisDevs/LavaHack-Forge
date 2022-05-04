package org.cubic.eventsystem;

import java.util.function.Predicate;

public interface EventBus {

    void subscribe(Class<?> cls);

    void subscribe(Object o);

    void unsubscribe(Class<?> cls);

    void unsubscribe(Object o);

    void post(Object event);

    default <T> void post(T event, Predicate<T>... filters){
        for(Predicate<T> filter : filters){
            if(!filter.test(event))
                return;
        }
        post(event);
    }

    static EventBus getDefault(){
        return new EventDispatcher();
    }
}
