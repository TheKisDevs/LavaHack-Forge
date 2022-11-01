package com.kisman.cc.settings.types;

import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;

import java.util.function.Function;

public class SettingPage<T, S extends Setting> {

    private final S setting;

    private final Function<S, T> value;

    private final Module module;

    public SettingPage(S setting, Function<S, T> value, Module module){
        this.setting = setting;
        this.value = value;
        this.module = module;
    }

    public SettingPage<T, S> page(T value, Setting... settings){
        for(Setting s : settings)
            this.module.register(s.setVisible(() -> this.value.apply(this.setting).equals(value)));
        return this;
    }

    public S getSetting() {
        return setting;
    }

    public Function<S, T> getValue() {
        return value;
    }

    public Module getModule() {
        return module;
    }
}
