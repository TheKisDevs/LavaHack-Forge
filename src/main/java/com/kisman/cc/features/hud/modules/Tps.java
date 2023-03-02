package com.kisman.cc.features.hud.modules;

import com.kisman.cc.features.hud.ShaderableHudModule;
import com.kisman.cc.features.subsystem.subsystems.TPSManager;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import net.minecraft.util.text.TextFormatting;

public class Tps extends ShaderableHudModule {
    public Tps() {
        super("Tps", "", true, false, false);
    }

    public void draw() {
        String text = TextFormatting.WHITE + "T/S: " + TextFormatting.GRAY + TPSManager.INSTANCE.getTps();

        setW(CustomFontUtil.getStringWidth(text));
        setH(CustomFontUtil.getFontHeight());

        shaderRender = () -> drawStringWithShadow(text, getX(), getY(), -1);
    }
}
