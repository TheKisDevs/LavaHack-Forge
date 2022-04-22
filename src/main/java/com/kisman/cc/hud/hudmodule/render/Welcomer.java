package com.kisman.cc.hud.hudmodule.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.hud.hudmodule.*;
import com.kisman.cc.module.client.HUD;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class Welcomer extends HudModule {
    public Welcomer() {
        super("Welcomer", HudCategory.RENDER, true);
    }

    public void onEnable() {
        setX(100);
        setY(2);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        int color = HUD.instance.astolfoColor.getValBoolean() ? ColorUtils.astolfoColors(100, 100) : new Color(HUD.instance.welColor.getR(), HUD.instance.welColor.getG(), HUD.instance.welColor.getB(), HUD.instance.welColor.getA()).getRGB();

        setW(CustomFontUtil.getStringWidth("Welcome to " + Kisman.getName() + ", " + mc.player.getName() + "!"));
        setH(CustomFontUtil.getFontHeight());

        CustomFontUtil.drawCenteredStringWithShadow("Welcome to " + Kisman.getName() + ", " + mc.player.getName() + "!", getX(), getY(), color);
    }
}
