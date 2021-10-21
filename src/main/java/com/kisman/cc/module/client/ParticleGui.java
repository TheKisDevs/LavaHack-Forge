package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ParticleGui extends Module {
    public static ParticleGui instance;

    public Setting rainbow = new Setting("RainBow", this, false);

    public ParticleGui() {
        super("Particle", "Particle", Category.CLIENT);

        instance = this;

        setmgr.rSetting(rainbow);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        Kisman.instance.particleSystem.render();
    }
}
