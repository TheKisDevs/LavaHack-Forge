package com.kisman.cc.features.macro;

import com.kisman.cc.features.macro.activator.Activator;
import com.kisman.cc.features.macro.activator.ActivatorFactory;
import com.kisman.cc.features.macro.activator.ActivatorManager;
import com.kisman.cc.features.macro.impl.MacroImpl;

import java.util.List;

/**
 * @author Cubic
 * @since 02.10.2022
 */
public class Macro {

    private final MacroImpl macro;

    private final List<Activator> activators;

    private final String name;

    public Macro(String name, MacroImpl macro, List<Activator> activators) {
        this.macro = macro;
        this.activators = activators;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public MacroImpl getMacro() {
        return macro;
    }

    public List<Activator> getActivators() {
        return activators;
    }

    public void enable(){
        activators.forEach(Activator::enable);
    }

    public void disable(){
        activators.forEach(Activator::disable);
    }

    public void addActivator(String name, String condition, boolean enable){
        ActivatorFactory<?> factory = ActivatorManager.getFactory(name);
        if(factory == null)
            return;
        Activator activator = factory.construct(condition, macro);
        if(enable)
            activator.enable();
        activators.add(activator);
    }

    public void addActivator(Class<?> cls, String condition, boolean enable){
        ActivatorFactory<?> factory = ActivatorManager.getFactory(cls);
        if(factory == null)
            return;
        Activator activator = factory.construct(condition, macro);
        if(enable)
            activator.enable();
        activators.add(activator);
    }
}
