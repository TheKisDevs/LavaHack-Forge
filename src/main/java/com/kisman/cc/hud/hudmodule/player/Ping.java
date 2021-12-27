package com.kisman.cc.hud.hudmodule.player;

import com.kisman.cc.Kisman;
import com.kisman.cc.hud.hudmodule.*;
import com.kisman.cc.module.client.HUD;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Ping extends HudModule {
    public Ping() {
        super("Ping", "", HudCategory.PLAYER);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        ScaledResolution sr = new ScaledResolution(mc);
        String str = "Ping: " + TextFormatting.GRAY + (mc.isSingleplayer() ? 0 : Kisman.instance.serverManager.getPing());
        int color = HUD.instance.astolfoColor.getValBoolean() ? ColorUtils.astolfoColors(100, 100) : -1;
        CustomFontUtil.drawStringWithShadow(str, sr.getScaledWidth() - 1 - CustomFontUtil.getStringWidth(str), sr.getScaledHeight() - 1 - CustomFontUtil.getFontHeight(), color);
    }
}
