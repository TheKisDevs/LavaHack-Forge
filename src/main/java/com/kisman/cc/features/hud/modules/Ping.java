package com.kisman.cc.features.hud.modules;

import com.kisman.cc.features.hud.ShaderableHudModule;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.UtilityKt;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import net.minecraft.util.text.TextFormatting;

public class Ping extends ShaderableHudModule {
    private final Setting astolfo = register(new Setting("Astolfo", this, true));

    public Ping() {
        super("Ping", "", true, false, false);
    }

    public void draw() {
        String str = "Ping: " + TextFormatting.GRAY + UtilityKt.getPing();
        setW(CustomFontUtil.getStringWidth(str));
        setH(CustomFontUtil.getFontHeight());

        shaderRender = () -> drawStringWithShadow(str, getX(), getY(), astolfo.getValBoolean() ? ColorUtils.astolfoColors(100, 100) : -1);
    }
}
