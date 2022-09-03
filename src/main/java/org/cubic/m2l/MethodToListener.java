package org.cubic.m2l;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listenable;
import me.zero.alpine.listener.Listener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MethodToListener implements Listenable {

    @EventHandler
    private final Listener<?> listener;

    private final Object listenable;

    private final M2LTarget m2LTarget;

    public MethodToListener(Object instance, final Method method, M2LTarget m2LTarget, Class<?> eventTarget){
        method.setAccessible(true);
        this.listener = new Listener<>(event -> {
            try {
                method.invoke(instance, event);
            } catch (Exception e){
                throw new IllegalStateException(e);
            }
        });
        setField(TARGET, listener, eventTarget);
        setField(PRIORITY, listener, m2LTarget.priority());
        this.listenable = instance;
        this.m2LTarget = m2LTarget;
    }

    public Listener<?> getListener() {
        return listener;
    }

    public Object getListenable() {
        return listenable;
    }

    public M2LTarget getM2LTarget(){
        return m2LTarget;
    }

    private static final Field TARGET;

    private static final Field PRIORITY;

    static {
        try {
            TARGET = Listener.class.getDeclaredField("target");
            TARGET.setAccessible(true);

            PRIORITY = Listener.class.getDeclaredField("priority");
            PRIORITY.setAccessible(true);
        } catch (Exception e){
            throw new IllegalStateException(e);
        }
    }

    private static void setField(Field f, Object o, Object value){
        try {
            f.set(o, value);
        } catch (Exception e){
            throw new IllegalStateException(e);
        }
    }
}
