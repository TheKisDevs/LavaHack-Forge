package com.kisman.cc.features.module.render.shader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.util.HashMap;
import java.util.Map;

public abstract class FramebufferShader extends Shader {
    protected static int lastScale;
    protected static int lastScaleWidth;
    protected static int lastScaleHeight;

    public static final Minecraft mc = Minecraft.getMinecraft();
    public static Framebuffer framebuffer;
    public boolean entityShadows;
    public int animationSpeed;

    private HashMap<String, Object> prevUniforms = new HashMap<>();
    private final HashMap<String, Object> currentUniforms = new HashMap<>();

    private boolean changeable;

    public FramebufferShader(String fragmentShader) {
        this(fragmentShader, true);
    }

    public FramebufferShader(String fragmentShader, boolean changeable) {
        super(fragmentShader);
        this.changeable = changeable;

        framebuffer = setupFrameBuffer(null);
    }

    protected boolean processUniforms() {
        for(Map.Entry<String, Object> entry : currentUniforms.entrySet()) {
            if(processUniform(entry.getKey(), entry.getValue())) {
                return true;
            }
        }

        return false;
    }

    protected boolean processUniform(String name, Object value) {
        return prevUniforms.containsKey(name) && !prevUniforms.get(name).equals(value);
    }

    public void changeUniform(String name, Object value) {
        currentUniforms.put(name, value);
    }

    public void swapUniforms() {
        prevUniforms = currentUniforms;
        currentUniforms.clear();
    }

    public void startDraw(float partialTicks) {
        GlStateManager.enableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();

        if(changeable || processUniforms()) {
            framebuffer = setupFrameBuffer(framebuffer);
        }

        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);

        entityShadows = mc.gameSettings.entityShadows;
        mc.gameSettings.entityShadows = false;
        mc.entityRenderer.setupCameraTransform(partialTicks, 0);
    }

    public void setup3() {
        framebuffer = setupFrameBuffer(framebuffer);
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
    }

    public void start3() {
        GlStateManager.enableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
    }

    public void stop3() {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        mc.getFramebuffer().bindFramebuffer(true);
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    public void drawFBO3() {
        GlStateManager.enableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        startShader();
        drawFramebuffer(framebuffer);
        stopShader();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    public void startDraw2() {
        GlStateManager.enableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();
        framebuffer = setupFrameBuffer(framebuffer);
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
    }

    public void stopDraw2() {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        mc.getFramebuffer().bindFramebuffer(true);
        startShader();
        drawFramebuffer(framebuffer);
        stopShader();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
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

