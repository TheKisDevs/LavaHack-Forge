package com.kisman.cc.hud.hudmodule.player;

import com.kisman.cc.hud.hudmodule.HudCategory;
import com.kisman.cc.hud.hudmodule.HudModule;
import com.kisman.cc.util.*;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HandProgress extends HudModule {
    private double width = 0;

    public HandProgress() {
        super("HandProgress", HudCategory.PLAYER);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        double cooldownPercent = MathHelper.clamp(mc.player.getItemInUseCount(), 0, mc.player.getItemInUseMaxCount());
        final double cdWidth = 100 * cooldownPercent / mc.player.getItemInUseMaxCount();
        width = AnimationUtils.animate(cdWidth, width, 0.06f);

        if(width > 0.061f) {
            Render2DUtil.drawRect(GLUtils.getScreenWidth() / 2 - 20,GLUtils.getScreenHeight() / 2 + 30,GLUtils.getScreenWidth() / 2 + 80,GLUtils.getScreenHeight() / 2 + 24, ColorUtils.getColor(11, 11, 11, 255));

            if(cdWidth > 95) {
                Render2DUtil.drawRect(GLUtils.getScreenWidth() / 2 - 20,GLUtils.getScreenHeight() / 2 + 30,GLUtils.getScreenWidth() / 2 + 80,GLUtils.getScreenHeight() / 2 + 24, ColorUtils.getColor(255, 0, 70, 255));
            }

            Render2DUtil.drawRect(GLUtils.getScreenWidth() / 2 - 19,GLUtils.getScreenHeight() / 2 - 19 + 48,GLUtils.getScreenWidth() / 2 - 19 + width,GLUtils.getScreenHeight() / 2 + 25, ColorUtils.getColor(255 - (int) cdWidth * 3, (int) cdWidth * 3, 0, 255));
        }
    }
}
