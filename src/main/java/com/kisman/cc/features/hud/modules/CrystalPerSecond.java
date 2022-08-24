package com.kisman.cc.features.hud.modules;

import com.kisman.cc.features.hud.HudModule;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.manager.Managers;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CrystalPerSecond extends HudModule {
    private final Setting astolfo = register(new Setting("Astolfo", this, true));
    private final Setting color = register(new Setting("Color",this, new Colour(255, 255, 255, 255)));

    public CrystalPerSecond() {
        super("CrystalPerSecond", true);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        int color = astolfo.getValBoolean() ? ColorUtils.astolfoColors(100, 100) : this.color.getColour().getRGB();
        String text = "Crystal/Sec: " + TextFormatting.GRAY + Managers.instance.cpsManager.getCPS();
        CustomFontUtil.drawStringWithShadow(text, getX(), getY(), color);

        setW(CustomFontUtil.getStringWidth(text));
        setH(CustomFontUtil.getFontHeight());
    }
}
