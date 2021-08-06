package com.kisman.cc.module.render;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class TestRenderModule extends Module {
    public TestRenderModule() {
        super("TestRenderModule", "", Category.RENDER);
    }

    @SubscribeEvent
    public static void renderBlockOutline(float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(1, 1, 1);
        GlStateManager.translate(-9, 63, 6);

        GlStateManager.disableCull();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();

        GlStateManager.color(255, 255, 255, 255);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

        bufferBuilder.pos(0, 0, 0).endVertex();
        bufferBuilder.pos(1, 0, 0).endVertex();
        bufferBuilder.pos(1, 0, 1).endVertex();
        bufferBuilder.pos(0, 0, 1).endVertex();

        bufferBuilder.pos(0, 1, 0).endVertex();
        bufferBuilder.pos(1, 1, 0).endVertex();
        bufferBuilder.pos(1, 1, 1).endVertex();
        bufferBuilder.pos(0, 1, 1).endVertex();

        tessellator.draw();

        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();

        GlStateManager.popMatrix();
    }

//    @SubscribeEvent
//    public void onWorldRender(RenderWorldLastEvent event) {
//        GL11.glPushMatrix();
//        GLUtil.setGLCap(3042, true);
//        GLUtil.setGLCap(3553, false);
//        GLUtil.setGLCap(2896, false);
//        GLUtil.setGLCap(2929, false);
//        GL11.glDepthMask(false);
//        GL11.glLineWidth(1.8f);
//        GL11.glBlendFunc(770, 771);
//        GLUtil.setGLCap(2848, true);
//        GL11.glColor4f(0.0f, 1.0f, 0.0f, 0.11f);
//        GL11.glDepthMask(true);
//        GLUtil.revertAllCaps();
//        GL11.glPopMatrix();
//    } else if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).hurtTime > 0) {
//        GL11.glColor4f(1.0f, 0.0f, 0.0f, 0.11f);
//    }
}
