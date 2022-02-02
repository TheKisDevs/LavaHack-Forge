package com.kisman.cc.module.render;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class EyeFinder extends Module {
    private Setting color = new Setting("Color", this, "Color", new Colour(Color.CYAN));
    private Setting range = new Setting("Range", this, 50, 20, 50, true);

    public EyeFinder() {
        super("EyeFinder", Category.RENDER);

        setmgr.rSetting(color);
        setmgr.rSetting(range);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        mc.world.loadedEntityList.stream().filter(entity -> mc.player != entity && !entity.isDead && entity instanceof EntityPlayer && mc.player.getDistance(entity) <= range.getValInt()).forEach(this::drawLine);
    }

    private void drawLine(final Entity e) {
        final RayTraceResult result = e.rayTrace(6.0, mc.getRenderPartialTicks());
        if (result == null) return;
        final Vec3d eyes = e.getPositionEyes(mc.getRenderPartialTicks());
        GL11.glPushMatrix();
        GlStateManager.enableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        final double posX = eyes.x - mc.getRenderManager().renderPosX;
        final double posY = eyes.y - mc.getRenderManager().renderPosY;
        final double posZ = eyes.z - mc.getRenderManager().renderPosZ;
        final double posX2 = result.hitVec.x - mc.getRenderManager().renderPosX;
        final double posY2 = result.hitVec.y - mc.getRenderManager().renderPosY;
        final double posZ2 = result.hitVec.z - mc.getRenderManager().renderPosZ;
        GL11.glColor4f(0.2f, 0.1f, 0.3f, 0.8f);
        GlStateManager.glLineWidth(1.5f);
        GL11.glBegin(1);
        GL11.glVertex3d(posX, posY, posZ);
        GL11.glVertex3d(posX2, posY2, posZ2);
        GL11.glVertex3d(posX2, posY2, posZ2);
        GL11.glVertex3d(posX2, posY2, posZ2);
        GL11.glEnd();
        if (result.typeOfHit == RayTraceResult.Type.BLOCK) RenderUtil.drawBlockESP(result.getBlockPos(), 51 / 255f, 25 / 255f, 73 / 255f);
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GL11.glPopMatrix();
    }
}
