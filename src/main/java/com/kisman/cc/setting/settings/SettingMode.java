package com.kisman.cc.setting.settings;

import com.kisman.cc.module.Module;
import com.kisman.cc.setting.Setting;

import java.util.ArrayList;

public class SettingMode extends Setting {
    public String value;
    public ArrayList<String> values;
    public int index;

    public SettingMode(String name, Module mod, String value, ArrayList<String> values) {
        this.name = name;
        this.mod = mod;
        this.value = value;
        this.values = values;
        this.type = Setting.Type.MODE;
    }

    public void increment() {
        index++;
        if(index > values.size()) {
            index = 0;
        }
        if(index < 0) {
            index = values.size() - 1;
        }
        value = values.get(index);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ArrayList<String> getValues() {
        return values;
    }

    public void setValues(ArrayList<String> values) {
        this.values = values;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
