package com.kisman.cc.hud.hudmodule.render;

import com.kisman.cc.hud.hudmodule.HudCategory;
import com.kisman.cc.hud.hudmodule.HudModule;
import com.kisman.cc.module.client.HUD;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Fps extends HudModule {
    public Fps() {
        super("Fps", "fuck you", HudCategory.RENDER);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent event) {
        if(event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            int color = HUD.instance.astolfoColor.getValBoolean() ? ColorUtils.astolfoColors(100, 100) : -1;
            CustomFontUtil.drawStringWithShadow(
                "FPS: " + 
                TextFormatting.GRAY + 
                Minecraft.getDebugFPS(), 
                1,
                //Kisman.instance.logo.getHeight(), 
                2 + CustomFontUtil.getFontHeight(),
                color
            );
        }
    }
}
