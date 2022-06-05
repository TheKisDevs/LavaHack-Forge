package com.kisman.cc.features.hud.modules;

import com.kisman.cc.features.hud.HudModule;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import com.kisman.cc.util.manager.Managers;

import com.kisman.cc.util.render.ColorUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CrystalPerSecond extends HudModule {
    public CrystalPerSecond() {
        super("CrystalPerSecond", true);
        super.setX(1);
        super.setY(1);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        String text = "Crystal/Sec: " + TextFormatting.GRAY + Managers.instance.cpsManager.getCPS();
        CustomFontUtil.drawStringWithShadow(text, getX(), getY(), ColorUtils.astolfoColors(100, 100));

        setW(CustomFontUtil.getStringWidth(text));
        setH(CustomFontUtil.getFontHeight());
    }
}
