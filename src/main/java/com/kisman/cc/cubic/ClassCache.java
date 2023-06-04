package com.kisman.cc.cubic;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

class ClassCache extends ConcurrentHashMap<String, Class<?>> {

    public ClassCache(ConcurrentHashMap<String, Class<?>> map){
        super(map);
    }

    @Override
    public Class<?> put(@NotNull String key, @NotNull Class<?> value) {
        if(CubicLoader.map.containsKey(key))
            return null;
        return super.put(key, value);
    }
}
