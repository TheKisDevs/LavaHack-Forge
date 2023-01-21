package com.kisman.cc.features.hud.modules;

import com.kisman.cc.features.hud.ShaderableHudModule;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.manager.Managers;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import net.minecraft.util.text.TextFormatting;

public class CrystalPerSecond extends ShaderableHudModule {
    private final Setting astolfo = register(new Setting("Astolfo", this, true));
    private final Setting color = register(new Setting("Color",this, new Colour(255, 255, 255, 255)));

    public CrystalPerSecond() {
        super("CrystalPerSecond", true, false, false);
    }

    public void handleRender() {
        int color = astolfo.getValBoolean() ? ColorUtils.astolfoColors(100, 100) : this.color.getColour().getRGB();
        String text = "Crystal/Sec: " + TextFormatting.GRAY + Managers.instance.cpsManager.getCPS();
        shaderRender = () -> drawStringWithShadow(text, getX(), getY(), color);

        setW(CustomFontUtil.getStringWidth(text));
        setH(CustomFontUtil.getFontHeight());
    }
}
