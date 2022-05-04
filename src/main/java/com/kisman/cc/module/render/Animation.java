package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventArmSwingAnimationEnd;
import com.kisman.cc.module.*;
import com.kisman.cc.gui.csgo.components.Slider;
import com.kisman.cc.settings.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.init.MobEffects;

public class Animation extends Module {
    public static Animation instance;

    public Setting speed = new Setting("Speed", this, 13, 1, 20, Slider.NumberType.INTEGER);

    public Animation() {
        super("Animation", Category.RENDER);

        instance = this;

        setmgr.rSetting(speed);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(swing);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(swing);
    }

    @EventHandler private final Listener<EventArmSwingAnimationEnd> swing = new Listener<>(event -> {
        if(mc.player.isPotionActive(MobEffects.HASTE)) event.setArmSwindAnimationEnd(Animation.instance.speed.getValInt() - (mc.player.getActivePotionEffect(MobEffects.HASTE).getAmplifier()));
        else event.setArmSwindAnimationEnd(mc.player.isPotionActive(MobEffects.MINING_FATIGUE) ? Animation.instance.speed.getValInt() + (mc.player.getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier() + 1) * 2 : Animation.instance.speed.getValInt());
    });
}
