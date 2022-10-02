package com.kisman.cc.features.macro.activator;

import com.kisman.cc.features.macro.impl.MacroImpl;

/**
 * This class determines when a certain macro should
 * be activated/active. For Example, an activator might
 * a key activator. That means that the underlying macro
 * will be activated when a certain key is pressed. Another
 * Example would be an EventActivator. The underlying macro
 * would be activated on a certain activator.
 *
 * @author Cubic
 * @since 02.10.2022
 */
public abstract class Activator {

    protected final String name;

    protected final String condition;

    protected final MacroImpl macro;

    protected boolean enabled;

    public Activator(String name, String condition, MacroImpl macro) {
        this.name = name;
        this.condition = condition;
        this.macro = macro;
        this.enabled = false;
    }

    public String getName() {
        return name;
    }

    public String getCondition() {
        return condition;
    }

    public MacroImpl getMacro() {
        return macro;
    }

    public final void enable(){
        this.enabled = true;
        onEnable();
    }

    public final void disable(){
        this.enabled = false;
        onDisable();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean hasStopped() {
        return !enabled;
    }

    protected void callMacro() {
        if (enabled)
            return;
        this.macro.execute();
    }

    protected void onEnable(){
    }

    protected void onDisable(){
    }
}
