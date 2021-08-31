package com.kisman.cc.hud.hudmodule.render;

import com.kisman.cc.hud.hudmodule.HudCategory;
import com.kisman.cc.hud.hudmodule.HudModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Fps extends HudModule {
    Minecraft mc = Minecraft.getMinecraft();
    FontRenderer fr = mc.fontRenderer;

    public Fps() {
        super("Fps", "fuck you", HudCategory.RENDER);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent event) {
        if(event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            fr.drawStringWithShadow(
                TextFormatting.WHITE + 
                "FPS: " + 
                TextFormatting.GRAY + 
                Minecraft.getDebugFPS(), 
                1,
                //Kisman.instance.logo.getHeight(), 
                2 + fr.FONT_HEIGHT,
                -1
            );
        }
    }
}
