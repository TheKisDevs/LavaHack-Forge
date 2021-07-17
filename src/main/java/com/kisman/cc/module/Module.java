package com.kisman.cc.module;

import com.kisman.cc.Kisman;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Module {
    public String name;
    public int key;
    public Category category;
    public boolean toggled;

    public Module(String name, int key, Category category) {
        Kisman.LOGGER.info("module init");
        this.name = name;
        this.key = key;
        this.category = category;
    }

    public void toggle() {
        toggled = !toggled;
        if(toggled) {
            enable();
        } else {
            disable();
        }
    }

    public void update() {}
    public void render() {}

    public void enable() {
        //Kisman.EVENT_BUS.subscribe(this);
        MinecraftForge.EVENT_BUS.register(this);
        onEnable();
    }
    public void disable() {
//        Kisman.EVENT_BUS.unsubscribe(this);
        MinecraftForge.EVENT_BUS.unregister(this);
        onDisable();
    }
    public void onEnable() {}
    public void onDisable() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }
}
