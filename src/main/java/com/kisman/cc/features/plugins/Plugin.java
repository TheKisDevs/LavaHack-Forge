package com.kisman.cc.features.plugins;

public interface Plugin {
    default void init() {}
    void load();
    void unload();
}
