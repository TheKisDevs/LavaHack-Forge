package com.kisman.cc.setting;

import com.kisman.cc.module.Module;

public class Setting{
    public String name;
    public Module mod;
    public Type type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Module getMod() {
        return mod;
    }

    public void setMod(Module mod) {
        this.mod = mod;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {
        BOOLEAN,
        DOUBLE,
        INTEGER,
        MODE
    }
}
