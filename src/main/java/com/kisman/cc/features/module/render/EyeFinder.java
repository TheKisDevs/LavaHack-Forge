package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.util.MultiThreaddableModulePattern;
import com.kisman.cc.util.*;
import com.kisman.cc.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.*;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class EyeFinder extends Module {
    private final Setting color = register(new Setting("Color", this, "Color", new Colour(Color.CYAN)));
    private final Setting range = register(new Setting("Range", this, 50, 20, 50, true));

    private final MultiThreaddableModulePattern multiThread = new MultiThreaddableModulePattern(this);

    private ArrayList<EntityPlayer> list = new ArrayList<>();

    public EyeFinder() {
        super("EyeFinder", Category.RENDER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        multiThread.reset();
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        multiThread.update(() -> {
            ArrayList<EntityPlayer> list = new ArrayList<>();

            for(EntityPlayer player : mc.world.playerEntities) if(player != mc.player && !player.isDead && mc.player.getDistanceSq(player) <= (range.getValDouble() * range.getValDouble())) list.add(player);

            mc.addScheduledTask(() -> this.list = list);
        });

        for(EntityPlayer player : list) {
            if(player == null) continue;
            final RayTraceResult result = player.rayTrace(6.0, mc.getRenderPartialTicks());
            if (result == null) return;
            final Vec3d eyes = player.getPositionEyes(mc.getRenderPartialTicks());
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
            color.getColour().glColor();
            GlStateManager.glLineWidth(1.5f);
            GL11.glBegin(1);
            GL11.glVertex3d(posX, posY, posZ);
            GL11.glVertex3d(posX2, posY2, posZ2);
            GL11.glVertex3d(posX2, posY2, posZ2);
            GL11.glVertex3d(posX2, posY2, posZ2);
            GL11.glEnd();
            if (result.typeOfHit == RayTraceResult.Type.BLOCK) RenderUtil.drawBlockESP(result.getBlockPos(), color.getColour().r1, color.getColour().g1, color.getColour().b1);
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GL11.glPopMatrix();
        }
    }
}
