package com.kisman.cc.hud.modules;

import com.kisman.cc.Kisman;
import com.kisman.cc.hud.HudModule;
import com.kisman.cc.util.customfont.CustomFontUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Tps extends HudModule {
    public Tps() {
        super("Tps", "");
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        ScaledResolution sr = new ScaledResolution(mc);
        String str = TextFormatting.WHITE + "TPS: " + TextFormatting.GRAY + Kisman.instance.serverManager.getTps();
        CustomFontUtil.drawStringWithShadow(str, sr.getScaledWidth() - 1 - CustomFontUtil.getStringWidth(str), sr.getScaledHeight() - 3 - (CustomFontUtil.getFontHeight() * 2), -1);
    }
}
