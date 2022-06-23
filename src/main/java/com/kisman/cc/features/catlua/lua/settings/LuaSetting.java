package com.kisman.cc.features.catlua.lua.settings;

import com.kisman.cc.features.catlua.lua.exception.LuaIllegalNumberTypeException;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.number.NumberType;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class LuaSetting {
    public static NumberType getNumberTypeByName(String name) {
        for(NumberType type : NumberType.values()) if(type.name().equals(name)) return type;
        throw new LuaIllegalNumberTypeException("Lua: cant resolve number type " + name);
    }

    public static final class LuaBuilder extends OneArgFunction {
        @Override public LuaValue call(LuaValue arg) {
            return CoerceJavaToLua.coerce(new Setting(arg.tojstring()));
        }
    }
}
