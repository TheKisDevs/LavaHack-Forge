package com.kisman.cc.hud.hudmodule.render;

import com.kisman.cc.hud.hudmodule.HudCategory;
import com.kisman.cc.hud.hudmodule.HudModule;
import com.kisman.cc.module.client.HUD;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class Welcomer extends HudModule {
    public Welcomer() {
        super("Welcomer", "", HudCategory.RENDER);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        ScaledResolution sr = new ScaledResolution(mc);
        int color = HUD.instance.astolfoColor.getValBoolean() ? ColorUtils.astolfoColors(100, 100) : new Color(HUD.instance.welColor.getR(), HUD.instance.welColor.getG(), HUD.instance.welColor.getB(), HUD.instance.welColor.getA()).getRGB();

        CustomFontUtil.drawStringWithShadow("Welcome " + mc.player.getName(), (sr.getScaledWidth() / 2) - (CustomFontUtil.getStringWidth("Welcome " + mc.player.getName()) / 2), 1, color);
    }
}
