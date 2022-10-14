package com.kisman.cc.features.module.combat.blocker;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.combat.Blocker;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;
import me.zero.alpine.listener.Listenable;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

import java.util.function.Supplier;

public abstract class BlockerModule implements Listenable {

    public static Supplier<Blocker> blockerSupplier;

    protected static final Minecraft mc = Minecraft.getMinecraft();

    protected Blocker blocker;

    protected SettingGroup group;

    private final String name;

    protected Setting active;

    private final boolean subscribes;

    private final boolean hasListeners;

    public BlockerModule(SettingGroup group, String name, boolean subscribes, boolean hasListeners){
        this.blocker = blockerSupplier.get();
        this.group = group;
        this.name = name;
        this.subscribes = subscribes;
        this.hasListeners = hasListeners;
        this.active = blocker.register(group.add(new Setting(this.name, blocker, false)));
    }

    public boolean isEnabled(){
        return active.getValBoolean();
    }

    protected Setting register(Setting setting){
        return blocker.register(group.add(setting.setVisible(active::getValBoolean)));
    }

    public void onEnable(){
        if(subscribes) MinecraftForge.EVENT_BUS.register(this);
        if(hasListeners) Kisman.EVENT_BUS.subscribe(this);
    }

    public void onDisable(){
        if(subscribes) MinecraftForge.EVENT_BUS.unregister(this);
        if(hasListeners) Kisman.EVENT_BUS.unsubscribe(this);
    }

    public void update(){
    }
}

