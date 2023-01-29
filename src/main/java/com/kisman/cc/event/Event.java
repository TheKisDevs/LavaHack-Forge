package com.kisman.cc.event;

import com.kisman.cc.Kisman;
import com.kisman.cc.util.client.collections.Bind;
import me.zero.alpine.event.type.Cancellable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class Event extends Cancellable {
    private boolean isPingBypass;

    public Event mirrorEvent = null;

    private Era era;

    public static HashMap<Class<? extends Event>, Bind<Boolean, Constructor<?>>> mirrorEventMap = new HashMap<>();

    public Event(Object... values) {
        if(!mirrorEventMap.containsKey(getClass())) {
            Class<?>[] classes = new Class<?>[] {};

            if(values.length != 0) {
                classes = new Class<?>[values.length];

                for (int i = 0; i < values.length; i++) classes[i] = values[i].getClass();
            }

            Constructor<?> mirrorConstructor = null;
            boolean hasMirrorEvent = true;

            try {
                mirrorConstructor = Class.forName("the.kis.devs.api.event.events." + Event.class.getSimpleName()).getConstructor(classes);
            } catch (NoSuchMethodException | ClassNotFoundException ignored) { }

            hasMirrorEvent = mirrorConstructor != null;

            mirrorEventMap.put(getClass(), new Bind<>(hasMirrorEvent, mirrorConstructor));
        }

        if(mirrorEventMap.containsKey(getClass())) if(mirrorEventMap.get(getClass()).getFirst() && mirrorEventMap.get(getClass()).getSecond() != null) try {
            mirrorEvent = (Event) mirrorEventMap.get(getClass()).getSecond().newInstance(values);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException ignored) { }
    }

    public Event(Era era, Object... values) {
        this(values);
        this.era = era;
    }
    public Era getEra() {return era;}
    public void setEra(Era era) {this.era = era;}

    public enum Era {
        PRE,
        POST,
        PERI
    }

    public LuaValue toLua() {return CoerceJavaToLua.coerce(this);}
    public String getName() {return "other_event";}
    public boolean isPre() {return era.equals(Era.PRE);}
    public boolean isPost() {return era.equals(Era.POST);}
    public String getEraString() {return era.name();}
    public void post() {Kisman.EVENT_BUS.post(this);}

    public boolean isPingBypass() {
        return isPingBypass;
    }

    public void setPingBypass(boolean pingBypass) {
        isPingBypass = pingBypass;
    }
}
