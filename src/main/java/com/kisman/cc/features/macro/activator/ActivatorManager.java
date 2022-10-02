package com.kisman.cc.features.macro.activator;

import com.kisman.cc.features.macro.activator.activators.KeyActivator;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Cubic
 * @since 02.10.2022
 */
public final class ActivatorManager {

    private static final Map<Entry, ActivatorFactory<?>> ACTIVATORS = new ConcurrentHashMap<>();

    public static ActivatorFactory<?> getFactory(String name){
        return ACTIVATORS.get(new Entry(name, null));
    }

    public static ActivatorFactory<?> getFactory(Class<?> cls){
        return ACTIVATORS.get(new Entry(null, cls));
    }

    static {
        ACTIVATORS.put(new Entry("key", KeyActivator.class), KeyActivator::new);
    }

    private static class Entry {

        private final String name;

        private final Class<?> cls;

        public Entry(String name, Class<?> cls) {
            this.name = name;
            this.cls = cls;
        }

        public String getName() {
            return name;
        }

        public Class<?> getCls() {
            return cls;
        }

        @Override
        public boolean equals(Object obj) {
            if(!(obj instanceof Entry))
                return false;
            Entry entry = (Entry) obj;
            return Objects.equals(name, entry.name) || Objects.equals(cls, entry.cls);
        }
    }
}
