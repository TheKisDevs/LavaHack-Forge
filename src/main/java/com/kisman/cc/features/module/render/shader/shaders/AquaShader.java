package com.kisman.cc.features.module.render.shader.shaders;

import com.kisman.cc.features.module.render.shader.FramebufferShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL20;

public class AquaShader extends FramebufferShader {
    public static AquaShader AQUA_SHADER;
    public float time;
    public float timeMult = 0.05f;

    public AquaShader() {
        super("aqua.frag");
    }

    @Override
    public void setupUniforms() {
        this.setupUniform("resolution");
        this.setupUniform("time");
    }

    @Override
    public void updateUniforms() {
        GL20.glUniform2f(this.getUniform("resolution"), (float)new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), (float)new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight());
        GL20.glUniform1f(this.getUniform("time"), this.time);
        time += timeMult * animationSpeed;
    }

    static {
        AquaShader.AQUA_SHADER = new AquaShader();
    }
}
