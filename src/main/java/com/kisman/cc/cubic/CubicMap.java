package com.kisman.cc.cubic;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CubicMap<K, V> extends ConcurrentHashMap<K, V> {

    @Override
    public KeySetView<K, V> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }
}
