package com.kisman.cc.hud.hudmodule.player;

import com.kisman.cc.Kisman;
import com.kisman.cc.hud.hudmodule.HudCategory;
import com.kisman.cc.hud.hudmodule.HudModule;
import com.kisman.cc.util.customfont.CustomFontUtil;
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
        String str = TextFormatting.WHITE + "Ping: " + TextFormatting.GRAY + (mc.isSingleplayer() ? 0 : Kisman.instance.serverManager.getPing());
        CustomFontUtil.drawStringWithShadow(str, sr.getScaledWidth() - 1 - CustomFontUtil.getStringWidth(str), sr.getScaledHeight() - 1 - CustomFontUtil.getFontHeight(), -1);
    }
}
