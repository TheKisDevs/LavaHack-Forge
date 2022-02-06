package com.kisman.cc.hud.hudmodule.combat;

import com.kisman.cc.hud.hudmodule.*;
import com.kisman.cc.module.client.HUD;
import com.kisman.cc.util.customfont.CustomFontUtil;
import com.kisman.cc.util.manager.Managers;

import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CrystalPerSecond extends HudModule {
    public CrystalPerSecond() {
        super("CrystalPerSecond", HudCategory.COMBAT);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        double yPos = HUD.instance.crystalpsY.getValDouble();
        CustomFontUtil.drawStringWithShadow("Average Time For Place: " + TextFormatting.GRAY + Managers.instance.crystalManager.getAverageTimeForPlace(), 1, yPos, ColorUtils.astolfoColors(100, 100));
        CustomFontUtil.drawStringWithShadow("Crystal Per Second: " + TextFormatting.GRAY + Managers.instance.crystalManager.getCrystalsPerSecond(), 1, yPos + CustomFontUtil.getFontHeight() + 2, ColorUtils.astolfoColors(100, 100));
    }
}
