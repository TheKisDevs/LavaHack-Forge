package com.kisman.cc.features.macro.impl;

import com.kisman.cc.features.macro.impl.impls.CommandMacro;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class MacroImplManager {

    private static final Map<Entry, MacroImplFactory<?>> MACRO_IMPLEMENTATIONS = new ConcurrentHashMap<>();

    public static MacroImplFactory<?> getMacroImpl(String name){
        return MACRO_IMPLEMENTATIONS.get(new Entry(name, null));
    }

    public static MacroImplFactory<?> getMacroImpl(Class<?> cls){
        return MACRO_IMPLEMENTATIONS.get(new Entry(null, cls));
    }

    static {
        MACRO_IMPLEMENTATIONS.put(new Entry("cmd", CommandMacro.class), CommandMacro::new);
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
