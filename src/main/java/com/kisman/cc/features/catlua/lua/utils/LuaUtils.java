package com.kisman.cc.features.catlua.lua.utils;

import com.kisman.cc.features.catlua.ScriptManager;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.util.Globals;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaValue;

import javax.annotation.Nullable;

public class LuaUtils implements Globals {
    public static void safeCall(@Nullable Module script, @Nullable LuaClosure closure, @Nullable LuaValue... values) {
        if (closure == null) return;
        try {
            if (values == null) closure.invoke();
            else closure.invoke(values);
        } catch (Throwable e) {
            if(mc.player != null && mc.world != null && script != null) ChatUtility.error().printClientModuleMessage(script.getName() + " -> " + e.getMessage());
            e.printStackTrace();
            if (script != null && !ScriptManager.strict) {
                script.setToggled(false);
            }
        }
    }

    public static void safeCall(LuaClosure closure, LuaValue... values) {
        safeCall(null, closure, values);
    }
}
