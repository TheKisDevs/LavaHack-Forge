package com.kisman.cc.hud.hudmodule.combat;

import com.kisman.cc.hud.hudmodule.HudCategory;
import com.kisman.cc.hud.hudmodule.HudModule;
import com.kisman.cc.util.TargetUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TargetInfo extends HudModule {
    public TargetInfo() {
        super("TargetInfo", "TargetInfo", HudCategory.COMBAT);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        ScaledResolution sr = new ScaledResolution(mc);
        FontRenderer fr = mc.fontRenderer;

        fr.drawStringWithShadow("TargetPlayer:", 1, sr.getScaledHeight() / 2, -1);
        fr.drawStringWithShadow(TextFormatting.WHITE + "Name: " + TextFormatting.GRAY + TargetUtil.getTarget().getName(), 1, sr.getScaledHeight() / 2 + 2 + fr.FONT_HEIGHT, -1);
        fr.drawStringWithShadow(TextFormatting.WHITE + "HP: " + TextFormatting.GRAY + TargetUtil.getTarget().getHealth(), 1, sr.getScaledHeight() / 2 + 4 + (fr.FONT_HEIGHT * 2), -1);
        fr.drawStringWithShadow(TextFormatting.WHITE + "Distance: " + TextFormatting.GRAY + TargetUtil.getTarget().getDistance(TargetUtil.getTarget()), 1, sr.getScaledHeight() / 2 + 6 + (fr.FONT_HEIGHT * 3), 1);
    }
}
