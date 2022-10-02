package com.kisman.cc.features.macro;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Cubic
 * @since 02.10.2022
 */
public final class MacroManager {

    private final static Map<String, Macro> MACROS = new ConcurrentHashMap<>();

    public static boolean addMacro(Macro macro){
        if(MACROS.containsKey(macro.getName()))
            return false;
        MACROS.put(macro.getName(), macro);
        return true;
    }

    public static boolean removeMacro(Macro macro){
        return MACROS.remove(macro.getName(), macro);
    }

    public static Macro removeMacro(String name){
        return MACROS.remove(name);
    }

    public Macro getMacro(String name){
        return MACROS.get(name);
    }

    public static Map<String, Macro> getMacros() {
        return MACROS;
    }
}
