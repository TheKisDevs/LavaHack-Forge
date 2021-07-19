package com.kisman.cc.setting.settings;

import com.kisman.cc.module.Module;
import com.kisman.cc.setting.Setting;

public class SettingInteger extends Setting {
    public int value;
    public int min;
    public int max;

    public SettingInteger(String name, Module mod, int value, int min, int max) {
        this.name = name;
        this.mod = mod;
        this.value = value;
        this.min = min;
        this.max = max;
        this.type = Setting.Type.INTEGER;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
