package com.kisman.cc.loader.security;

import com.kisman.cc.loader.LoaderKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.kisman.cc.loader.security.UnsafeProvider.unsafe;

// only Loader.kt can use this map
public class SecureMap<K, V> implements Map<K, V> {

    private final long address;

    public SecureMap(boolean concurrent){
        this.address = Unsecure.getAddress(concurrent ? new ConcurrentHashMap<K, V>() : new HashMap<K, V>());
    }

    private Map<K, V> get(){
        if(ClassContext.getCallerClass(2) != SecureMap.class || ClassContext.getCallerClass(3) != LoaderKt.class)
            throw new SecurityException("You cannot access this method");
        Object[] object = new Object[]{null};
        long baseOffset = unsafe.arrayBaseOffset(Object.class);
        unsafe.putLong(object, baseOffset, address);
        return (Map<K, V>) object[0];
    }

    @Override
    public int size() {
        return get().size();
    }

    @Override
    public boolean isEmpty() {
        return get().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return get().containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return get().containsValue(value);
    }

    @Override
    public V get(Object key) {
        return get().get(key);
    }

    @Nullable
    @Override
    public V put(K key, V value) {
        return get().put(key, value);
    }

    @Override
    public V remove(Object key) {
        return get().remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        get().putAll(m);
    }

    @Override
    public void clear() {
        get().clear();
    }

    @NotNull
    @Override
    public Set<K> keySet() {
        return get().keySet();
    }

    @NotNull
    @Override
    public Collection<V> values() {
        return get().values();
    }

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return get().entrySet();
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return get().getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        get().forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        get().replaceAll(function);
    }

    @Nullable
    @Override
    public V putIfAbsent(K key, V value) {
        return get().putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return get().remove(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return get().replace(key, oldValue, newValue);
    }

    @Nullable
    @Override
    public V replace(K key, V value) {
        return get().replace(key, value);
    }

    @Override
    public V computeIfAbsent(K key, @NotNull Function<? super K, ? extends V> mappingFunction) {
        return get().computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfPresent(K key, @NotNull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return get().computeIfPresent(key, remappingFunction);
    }

    @Override
    public V compute(K key, @NotNull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return get().compute(key, remappingFunction);
    }

    @Override
    public V merge(K key, @NotNull V value, @NotNull BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return get().merge(key, value, remappingFunction);
    }
}
