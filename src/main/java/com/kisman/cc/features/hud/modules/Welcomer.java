package com.kisman.cc.features.hud.modules;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.hud.HudModule;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import com.kisman.cc.util.render.ColorUtils;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Welcomer extends HudModule {
    private final Setting astolfo = register(new Setting("Astolfo", this, true));
    private final Setting color = register(new Setting("Color", this, "Color", new Colour(-1)));

    public Welcomer() {
        super("Welcomer", true);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        int color = astolfo.getValBoolean() ? ColorUtils.astolfoColors(100, 100) : this.color.getColour().getRGB();

        setW(CustomFontUtil.getStringWidth("Welcome to " + Kisman.getName() + ", " + mc.player.getName() + "!"));
        setH(CustomFontUtil.getFontHeight());

        CustomFontUtil.drawStringWithShadow("Welcome to " + Kisman.getName() + ", " + mc.player.getName() + "!", getX(), getY(), color);
    }
}
