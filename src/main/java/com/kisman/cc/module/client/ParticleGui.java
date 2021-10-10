package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ParticleGui extends Module {
    public static ParticleGui instance;

    public ParticleGui() {
        super("Particle", "Particle", Category.CLIENT);

        instance = this;
    }

    @SubscribeEvent
    public void draw(GuiScreenEvent.DrawScreenEvent event) {
        if(mc.player != null && mc.world != null) {
            Kisman.instance.particles.publish(new ScaledResolution(Minecraft.getMinecraft()), event.getMouseX(), event.getMouseY());
        }
    }
}
