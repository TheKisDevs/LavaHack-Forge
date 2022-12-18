package com.kisman.cc.event;

import com.kisman.cc.Kisman;
import me.zero.alpine.event.type.Cancellable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.lang.reflect.InvocationTargetException;

public class Event extends Cancellable {
    private boolean isPingBypass;

    public Event mirrorEvent = null;

    private Era era;
    public Event(Object... values) {
        try {
            Class<?> clazz = Class.forName("the.kis.devs.api.event.events." + getClass().getSimpleName());

            Class<?>[] classes = new Class<?>[values.length];

            for(int i = 0; i < values.length; i++) {
                classes[i] = values[i].getClass();
            }

            mirrorEvent = (Event) clazz.getConstructor(classes).newInstance(values);
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException ignored) {}
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
