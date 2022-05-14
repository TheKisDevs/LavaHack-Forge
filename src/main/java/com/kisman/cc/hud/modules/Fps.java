package com.kisman.cc.hud.modules;

import com.kisman.cc.hud.HudCategory;
import com.kisman.cc.hud.HudModule;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.customfont.CustomFontUtil;
import com.kisman.cc.util.render.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Fps extends HudModule {
    private final Setting astolfo = register(new Setting("Astolfo", this, true));

    public Fps() {
        super("Fps", HudCategory.RENDER, true);

        setX(1);
        setY(1);
    }

    public void update() {
        setW(CustomFontUtil.getStringWidth("Fps: " + Minecraft.getDebugFPS()));
        setH(CustomFontUtil.getFontHeight());
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        if(event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            int color = astolfo.getValBoolean() ? ColorUtils.astolfoColors(100, 100) : -1;
            CustomFontUtil.drawStringWithShadow("Fps: " + TextFormatting.GRAY + Minecraft.getDebugFPS(), getX(), getY(), color);
        }
    }
}
