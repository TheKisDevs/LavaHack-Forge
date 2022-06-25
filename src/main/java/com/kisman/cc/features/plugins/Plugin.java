package com.kisman.cc.features.plugins;

public abstract class Plugin {
    public void init() {}
    public abstract void load();
    public abstract void unload();
}
