package com.kisman.cc.features.module.render.shader.shaders;

import com.kisman.cc.features.module.render.shader.FramebufferShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL20;

public class GamerShader extends FramebufferShader {
    public static GamerShader GAMER_SHADER;
    public float time;
    public float timeMult = 0.03f;

    public GamerShader() {
        super("gamer.frag");
    }

    @Override
    public void setupUniforms() {
        this.setupUniform("resolution");
        this.setupUniform("time");
    }

    @Override
    public void updateUniforms() {
        GL20.glUniform2f(this.getUniform("resolution"), (float)new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), (float)new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight());
        GL20.glUniform1f(this.getUniform("time"), time);
        time += timeMult * animationSpeed;
    }

    static {
        GAMER_SHADER = new GamerShader();
    }
}
