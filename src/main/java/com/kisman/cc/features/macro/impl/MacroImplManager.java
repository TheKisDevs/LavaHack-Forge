package com.kisman.cc.features.macro.impl;

import com.kisman.cc.features.macro.impl.impls.CommandMacro;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MacroImplManager {

    private static final Map<String, MacroImplFactory<?>> MACRO_IMPLEMENTATIONS_STRINGS = new ConcurrentHashMap<>();

    private static final Map<Class<?>, MacroImplFactory<?>> MACRO_IMPLEMENTATIONS_CLASSES = new ConcurrentHashMap<>();

    public static MacroImplFactory<?> getMacroImpl(String name){
        return MACRO_IMPLEMENTATIONS_STRINGS.get(name);
    }

    public static MacroImplFactory<?> getMacroImpl(Class<?> cls){
        return MACRO_IMPLEMENTATIONS_CLASSES.get(cls);
    }

    static {
        MACRO_IMPLEMENTATIONS_STRINGS.put("cmd", CommandMacro::new);
        MACRO_IMPLEMENTATIONS_CLASSES.put(CommandMacro.class, CommandMacro::new);
    }
}
