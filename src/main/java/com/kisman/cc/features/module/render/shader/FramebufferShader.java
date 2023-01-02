package com.kisman.cc.features.module.render.shader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.*;

public abstract class FramebufferShader extends Shader {
    protected static int lastScale;
    protected static int lastScaleWidth;
    protected static int lastScaleHeight;

    public static final Minecraft mc = Minecraft.getMinecraft();
    public static Framebuffer framebuffer;
    public boolean entityShadows;
    public int animationSpeed;

    public FramebufferShader(String fragmentShader) {
        super(fragmentShader);
    }

    public void startDraw(float partialTicks) {
        GlStateManager.enableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        framebuffer = setupFrameBuffer(framebuffer);
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        entityShadows = mc.gameSettings.entityShadows;
        mc.gameSettings.entityShadows = false;
        mc.entityRenderer.setupCameraTransform(partialTicks, 0);
    }

    public void stopDraw() {
        mc.gameSettings.entityShadows = this.entityShadows;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        mc.getFramebuffer().bindFramebuffer(true);
        mc.entityRenderer.disableLightmap();
        RenderHelper.disableStandardItemLighting();
        startShader();
        mc.entityRenderer.setupOverlayRendering();
        drawFramebuffer(framebuffer);
        stopShader();
        mc.entityRenderer.disableLightmap();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    public Framebuffer setupFrameBuffer(Framebuffer frameBuffer) {
        if (Display.isActive() || Display.isVisible()) {
            if (frameBuffer != null) {
                frameBuffer.framebufferClear();

                ScaledResolution sr = new ScaledResolution(mc);
                int scaleFactor = sr.getScaleFactor();
                int widthFactor = sr.getScaledWidth();
                int heightFactor = sr.getScaledHeight();

                if (lastScale != scaleFactor || lastScaleWidth != widthFactor || lastScaleHeight != heightFactor) {
                    frameBuffer.deleteFramebuffer();
                    frameBuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
                    frameBuffer.framebufferClear();
                }

                lastScale = scaleFactor;
                lastScaleWidth = widthFactor;
                lastScaleHeight = heightFactor;
            } else frameBuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
        } else if (frameBuffer == null) frameBuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);


        return frameBuffer;
    }

    public void drawFramebuffer(Framebuffer framebuffer) {
        ScaledResolution sr = new ScaledResolution(mc);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebuffer.framebufferTexture);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2d(0, 1);
        GL11.glVertex2d(0, 0);
        GL11.glTexCoord2d(0, 0);
        GL11.glVertex2d(0, sr.getScaledHeight());
        GL11.glTexCoord2d(1, 0);
        GL11.glVertex2d(sr.getScaledWidth(), sr.getScaledHeight());
        GL11.glTexCoord2d(1, 1);
        GL11.glVertex2d(sr.getScaledWidth(), 0);
        GL11.glEnd();
        GL20.glUseProgram(0);
    }
}

