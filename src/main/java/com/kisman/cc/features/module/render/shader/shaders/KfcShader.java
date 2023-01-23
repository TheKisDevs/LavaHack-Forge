package com.kisman.cc.features.module.render.shader.shaders;

import com.kisman.cc.features.module.render.shader.FramebufferShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL20;

public class KfcShader extends FramebufferShader {
    public static KfcShader KFC_SHADER;
    public float time;
    public float timeMult = 0.01f;
    public float radius = 5;
    public float quality = 1;

    public KfcShader() {
        super("kfc.frag");
    }

    @Override
    public void setupUniforms() {
        setupUniforms("resolution", "time", "radius", "quality");
    }

    @Override
    public void updateUniforms() {
        GL20.glUniform2f(getUniform("resolution"), (float)new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), (float)new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight());
        GL20.glUniform1f(getUniform("time"), time);
        GL20.glUniform1f(getUniform("radius"), radius);
        GL20.glUniform1f(getUniform("quality"), quality);

        time += timeMult * animationSpeed;
    }

    static {
        KFC_SHADER = new KfcShader();
    }
}
