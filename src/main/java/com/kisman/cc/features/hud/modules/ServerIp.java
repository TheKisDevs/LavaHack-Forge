package com.kisman.cc.features.hud.modules;

import com.kisman.cc.features.hud.HudModule;
import com.kisman.cc.settings.util.HudModuleColorPattern;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ServerIp extends HudModule {
    private final HudModuleColorPattern color = colors();

    public ServerIp() {
        super("ServerIp", "", true);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        setW(CustomFontUtil.getStringWidth("Server: " + (mc.isSingleplayer() ? "SingePlayer" : mc.getCurrentServerData().serverIP.toLowerCase())));
        setH(CustomFontUtil.getFontHeight());

        CustomFontUtil.drawStringWithShadow("Server: " + TextFormatting.GRAY + (mc.isSingleplayer() ? "SingePlayer" : mc.getCurrentServerData().serverIP.toLowerCase()), getX(), getY(), color.color().getRGB());
    }
}
