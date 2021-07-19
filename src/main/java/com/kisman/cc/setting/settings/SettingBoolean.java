package com.kisman.cc.setting.settings;

import com.kisman.cc.module.Module;
import com.kisman.cc.setting.Setting;

public class SettingBoolean extends Setting {
    public boolean value;

    public SettingBoolean(String name, Module mod, boolean value) {
        this.name = name;
        this.mod = mod;
        this.value = value;
        this.type = Setting.Type.BOOLEAN;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
