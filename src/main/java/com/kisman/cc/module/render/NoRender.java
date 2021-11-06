package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoRender extends Module {
    public static NoRender instance;

    public Setting hurtCam = new Setting("HurtCam", this, false);
    public Setting armor = new Setting("Armor", this, false);
    public Setting overlay = new Setting("Overlay", this, false);

    public NoRender() {
        super("NoRender", "no render", Category.RENDER);

        instance = this;

        setmgr.rSetting(hurtCam);
        setmgr.rSetting(armor);
        setmgr.rSetting(overlay);
        Kisman.instance.settingsManager.rSetting(new Setting("Potion", this, false));
        Kisman.instance.settingsManager.rSetting(new Setting("Weather", this, false));
        Kisman.instance.settingsManager.rSetting(new Setting("Block", this, false));
        Kisman.instance.settingsManager.rSetting(new Setting("Lava", this, false));
    }

    public void update() {
        boolean potion = Kisman.instance.settingsManager.getSettingByName(this, "Potion").getValBoolean();
        boolean weather = Kisman.instance.settingsManager.getSettingByName(this, "Weather").getValBoolean();
        if(mc.player != null && mc.world != null) {
            if(potion) {
                if(mc.player.isPotionActive(Potion.getPotionById(25))) {
                    mc.player.removeActivePotionEffect(Potion.getPotionById(25));
                }
                if(mc.player.isPotionActive(Potion.getPotionById(2))) {
                    mc.player.removeActivePotionEffect(Potion.getPotionById(2));
                }
                if(mc.player.isPotionActive(Potion.getPotionById(4))) {
                    mc.player.removeActivePotionEffect(Potion.getPotionById(4));
                }
                if(mc.player.isPotionActive(Potion.getPotionById(9))) {
                    mc.player.removeActivePotionEffect(Potion.getPotionById(9));
                }
                if(mc.player.isPotionActive(Potion.getPotionById(15))) {
                    mc.player.removeActivePotionEffect(Potion.getPotionById(15));
                }
                if(mc.player.isPotionActive(Potion.getPotionById(17))) {
                    mc.player.removeActivePotionEffect(Potion.getPotionById(17));
                }
                if(mc.player.isPotionActive(Potion.getPotionById(18))) {
                    mc.player.removeActivePotionEffect(Potion.getPotionById(18));
                }
                if(mc.player.isPotionActive(Potion.getPotionById(27))) {
                    mc.player.removeActivePotionEffect(Potion.getPotionById(27));
                }
                if(mc.player.isPotionActive(Potion.getPotionById(20))) {
                    mc.player.removeActivePotionEffect(Potion.getPotionById(20));
                }
            }
            if(weather)
                mc.world.setRainStrength(0.0f);
        }
    }

    @SubscribeEvent
    public void renderBlockEvent(RenderBlockOverlayEvent event) {
        boolean block = Kisman.instance.settingsManager.getSettingByName(this, "Block").getValBoolean();
        boolean lava = Kisman.instance.settingsManager.getSettingByName(this, "Lava").getValBoolean();
        if(mc.player != null && mc.world != null) {
            if(block)
                event.setCanceled(true);
            if(lava)
                event.setCanceled(true);
        }
    }
}
