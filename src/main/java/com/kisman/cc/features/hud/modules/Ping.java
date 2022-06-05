package com.kisman.cc.features.hud.modules;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.hud.HudModule;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import com.kisman.cc.util.render.ColorUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Ping extends HudModule {
    private final Setting astolfo = register(new Setting("Astolfo", this, true));

    public Ping() {
        super("Ping", "", true);
        setX(1);
        setY(1);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        String str = "Ping: " + TextFormatting.GRAY + (mc.isSingleplayer() ? 0 : Kisman.instance.serverManager.getPing());
        setW(CustomFontUtil.getStringWidth(str));
        setH(CustomFontUtil.getFontHeight());
        CustomFontUtil.drawStringWithShadow(str, getX(), getY(), astolfo.getValBoolean() ? ColorUtils.astolfoColors(100, 100) : -1);
    }
}
