package com.kisman.cc.features.module.render.shader.shaders;

import com.kisman.cc.features.module.render.shader.FramebufferShader;
import org.lwjgl.opengl.GL20;

public final class OutlineShader extends FramebufferShader {
    public static final OutlineShader OUTLINE_SHADER = new OutlineShader();
    public float radius, quality, red,  green, blue, alpha, rainbowSpeed, rainbowStrength, saturation;

    public OutlineShader() {
        super("outline_custom.frag");
    }

    @Override
    public void setupUniforms() {
        setupUniform("texelSize");
        setupUniform("color");
        setupUniform("divider");
        setupUniform("radius");
        setupUniform("maxSample");
        setupUniform("rainbowStrength");
        setupUniform("rainbowSpeed");
        setupUniform("saturation");
    }

    @Override
    public void updateUniforms() {
        GL20.glUniform2f(getUniform("texelSize"), 1F / mc.displayWidth * (radius * quality), 1F / mc.displayHeight * (radius * quality));
        GL20.glUniform4f(getUniform("color"), red, green, blue, alpha);
        GL20.glUniform1f(getUniform("radius"), radius);
        GL20.glUniform2f(getUniform("rainbowStrength"), rainbowStrength, rainbowStrength);
        GL20.glUniform1f(getUniform("rainbowSpeed"), rainbowSpeed);
        GL20.glUniform1f(getUniform("saturation"), saturation);
    }
}