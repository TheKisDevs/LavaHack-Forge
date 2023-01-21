package com.kisman.cc.features.hud.modules;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.hud.ShaderableHudModule;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.customfont.CustomFontUtil;

public class Welcomer extends ShaderableHudModule {
    private final Setting astolfo = register(new Setting("Astolfo", this, true));
    private final Setting color = register(new Setting("Color", this, "Color", new Colour(-1)));

    public Welcomer() {
        super("Welcomer", true, false, false);
    }

    public void handleRender() {
        int color = astolfo.getValBoolean() ? ColorUtils.astolfoColors(100, 100) : this.color.getColour().getRGB();

        setW(CustomFontUtil.getStringWidth("Welcome to " + Kisman.getName() + ", " + mc.player.getName() + "!"));
        setH(CustomFontUtil.getFontHeight());

        shaderRender = () -> drawStringWithShadow("Welcome to " + Kisman.getName() + ", " + mc.player.getName() + "!", getX(), getY(), color);
    }
}
