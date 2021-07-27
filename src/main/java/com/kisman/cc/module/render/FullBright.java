package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class FullBright extends Module {
    public FullBright() {
        super("FullBright", "Gamma setting", Category.RENDER);
        Kisman.instance.settingsManager.rSetting(new Setting("Gamma", this, 1, 1, 100, true));
    }

    @SubscribeEvent
    public void update(TickEvent.ClientTickEvent event) {
        int gamma = (int) Kisman.instance.settingsManager.getSettingByName(this, "Gamma").getValDouble();
        Minecraft.getMinecraft().gameSettings.gammaSetting = gamma;
    }

    public void onDisable() {
        Minecraft.getMinecraft().gameSettings.gammaSetting = 1;
    }
}
