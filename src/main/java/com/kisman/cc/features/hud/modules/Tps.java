package com.kisman.cc.features.hud.modules;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.hud.ShaderableHudModule;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextFormatting;

public class Tps extends ShaderableHudModule {
    public Tps() {
        super("Tps", "", false, false, false);
    }

    public void handleRender() {
        ScaledResolution sr = new ScaledResolution(mc);
        String str = TextFormatting.WHITE + "TPS: " + TextFormatting.GRAY + Kisman.instance.serverManager.getTps();
        shaderRender = () -> drawStringWithShadow(str, sr.getScaledWidth() - 1 - CustomFontUtil.getStringWidth(str), sr.getScaledHeight() - 3 - (CustomFontUtil.getFontHeight() * 2), -1);
    }
}
