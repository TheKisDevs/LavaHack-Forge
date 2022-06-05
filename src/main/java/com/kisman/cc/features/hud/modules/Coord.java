package com.kisman.cc.features.hud.modules;

import com.kisman.cc.features.hud.HudModule;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import com.kisman.cc.util.render.ColorUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Coord extends HudModule {
    int posX, nPosX, posY, nPosY, posZ, nPosZ;

    public final Setting astolfo = register(new Setting("Astolfo", this, true));

    public Coord() {
        super("Coords", "coord");
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        ScaledResolution sr = new ScaledResolution(mc);

        if(event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            if(mc.player.dimension == 0) {
                posX = (int) mc.player.posX;
                posY = (int) mc.player.posY;
                posZ = (int) mc.player.posZ;
                nPosX = (int) (mc.player.posX / 8);
                nPosY = (int) (mc.player.posY / 8);
                nPosZ = (int) (mc.player.posZ / 8);
            } else if(mc.player.dimension == -1) {
                posX = (int) mc.player.posX;
                posY = (int) mc.player.posY;
                posZ = (int) mc.player.posZ;
                nPosX = (int) (mc.player.posX * 8);
                nPosY = (int) (mc.player.posY * 8);
                nPosZ = (int) (mc.player.posZ * 8);
            }

            int color = astolfo.getValBoolean() ? ColorUtils.astolfoColors(100, 100) : -1;
            String coordString = "X: " + "(" + posX + ")[" + nPosX + "]" + " Y: " + "(" + posY + ")[" + nPosY + "]" + " Z: " + "(" + posZ + ")[" + nPosZ + "]";
            String rotationString = "Yaw: " + ((int) MathHelper.wrapDegrees(mc.player.rotationYaw)) + " Pitch: " + (int) mc.player.rotationPitch;

            CustomFontUtil.drawStringWithShadow(coordString, 1, sr.getScaledHeight() - CustomFontUtil.getFontHeight() - 1, color);
            CustomFontUtil.drawStringWithShadow(rotationString, 1, sr.getScaledHeight() - (CustomFontUtil.getFontHeight() * 2) - 5, color);
        }
    }
}
