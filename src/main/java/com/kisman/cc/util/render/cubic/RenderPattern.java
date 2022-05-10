package com.kisman.cc.util.render.cubic;

import com.kisman.cc.Kisman;
import com.kisman.cc.settings.Setting;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public abstract class RenderPattern {

    private static final Map<String, Callable<RenderPattern>> instances = new HashMap<>(64);

    private final Map<String, Setting> settings = new HashMap<>();

    @Deprecated
    public static RenderPattern getInstance(String instance){
        try {
            RenderPattern pattern = instances.get(instance).call();
            pattern.initialize();
            return pattern;
        } catch (Exception e){
            return null;
        }
    }

    @Deprecated
    public static void rInstance(String name, Callable<RenderPattern> constructor){
        instances.put(name, constructor);
    }

    public RenderPattern(){

    }

    protected final Setting rSetting(String id, Setting setting){
        Kisman.instance.settingsManager.rSetting(setting);
        settings.put(id, setting);
        return setting;
    }

    public RenderPattern init(){
        initialize();
        return this;
    }

    protected void initialize(){
    }

    public final Setting getSetting(String id){
        return settings.get(id);
    }

    public abstract RenderBuilder getRenderBuilder();
}
