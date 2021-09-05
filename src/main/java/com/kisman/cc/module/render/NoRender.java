package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.init.MobEffects;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoRender extends Module {
    public NoRender() {
        super("NoRender", "no render", Category.RENDER);
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
                mc.player.removePotionEffect(MobEffects.BLINDNESS);
                mc.player.removePotionEffect(MobEffects.NAUSEA);
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
