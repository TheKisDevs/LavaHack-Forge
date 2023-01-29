package com.kisman.cc.features.hud.modules;

import com.kisman.cc.features.hud.ShaderableHudModule;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

public class Fps extends ShaderableHudModule {
    private final Setting astolfo = register(new Setting("Astolfo", this, true));

    public Fps() {
        super("Fps", "no fps no fun", true, false, false);
    }

    @Override
    public void draw() {
        setW(CustomFontUtil.getStringWidth("Fps: " + Minecraft.getDebugFPS()));
        setH(CustomFontUtil.getFontHeight());

        int color = astolfo.getValBoolean() ? ColorUtils.astolfoColors(100, 100) : -1;

        shaderRender = () -> drawStringWithShadow("Fps: " + TextFormatting.GRAY + Minecraft.getDebugFPS(), getX(), getY(), color);
    }
}
