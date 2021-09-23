package com.kisman.cc.hud.hudmodule.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.hud.hudmodule.HudCategory;
import com.kisman.cc.hud.hudmodule.HudModule;
import com.kisman.cc.util.MoveUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Speed extends HudModule {
    private int i;

    public Speed() {
        super("Speed", "Speed", HudCategory.MOVEMENT);
    }

    public void update() {
        if(Kisman.instance.hudModuleManager.getModule("Coords").isToggled()) {
            i = 1;
        }
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        FontRenderer fr = mc.fontRenderer;
        ScaledResolution sr = new ScaledResolution(mc);

        fr.drawStringWithShadow(TextFormatting.WHITE + "Speed: " + TextFormatting.GRAY + "b/s " + MoveUtil.getSpeed(), 1, sr.getScaledHeight() - 1 - fr.FONT_HEIGHT - (this.i == 1 ? (fr.FONT_HEIGHT * 2 + 4) : 0), -1);
    }
}
