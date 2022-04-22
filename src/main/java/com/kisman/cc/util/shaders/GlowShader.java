package com.kisman.cc.util.shaders;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.*;

public final class GlowShader extends FramebufferShader {
    private Minecraft mc = Minecraft.getMinecraft();
    public static final GlowShader INSTANCE;
    
    public GlowShader() {
        super("glow.frag");
    }
    
    public void setupUniforms() {
        this.setupUniform("texture");
        this.setupUniform("texelSize");
        this.setupUniform("color");
        this.setupUniform("divider");
        this.setupUniform("radius");
        this.setupUniform("maxSample");
    }
    
    public void updateUniforms() {
        GL20.glUniform1i(this.getUniform("texture"), 0);
        GL20.glUniform2f(this.getUniform("texelSize"), 1.0f / mc.displayWidth * (this.radius * this.quality), 1.0f / mc.displayHeight * (this.radius * this.quality));
        GL20.glUniform3f(this.getUniform("color"), this.red, this.green, this.blue);
        GL20.glUniform1f(this.getUniform("divider"), 140.0f);
        GL20.glUniform1f(this.getUniform("radius"), this.radius);
        GL20.glUniform1f(this.getUniform("maxSample"), 10.0f);
    }
    
    static {
        INSTANCE = new GlowShader();
    }
}