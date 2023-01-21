package com.kisman.cc.features.hud.modules;

import com.kisman.cc.features.hud.ShaderableHudModule;
import com.kisman.cc.settings.util.HudModuleColorPattern;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import net.minecraft.util.text.TextFormatting;

public class ServerIp extends ShaderableHudModule {
    private final HudModuleColorPattern color = colors();

    public ServerIp() {
        super("ServerIp", "", true, false, false);
    }

    public void handleRender() {
        setW(CustomFontUtil.getStringWidth("Server: " + (mc.isSingleplayer() ? "SingePlayer" : mc.getCurrentServerData().serverIP.toLowerCase())));
        setH(CustomFontUtil.getFontHeight());

        shaderRender = () -> drawStringWithShadow("Server: " + TextFormatting.GRAY + (mc.isSingleplayer() ? "SingePlayer" : mc.getCurrentServerData().serverIP.toLowerCase()), getX(), getY(), color.color().getRGB());
    }
}
