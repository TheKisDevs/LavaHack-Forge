package com.kisman.cc.util.shaders;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import org.lwjgl.opengl.*;

public class FlowShader extends FramebufferShader {
    private Minecraft mc = Minecraft.getMinecraft();
    public static final FlowShader INSTANCE;
    public float time;
    
    public FlowShader() {
        super("flow.frag");
    }
    
    public void setupUniforms() {
        this.setupUniform("resolution");
        this.setupUniform("time");
    }
    
    public void updateUniforms() {
        GL20.glUniform2f(this.getUniform("resolution"), (float)new ScaledResolution(mc).getScaledWidth(), (float)new ScaledResolution(mc).getScaledHeight());
        GL20.glUniform1f(this.getUniform("time"), 1.0f);
    }
    
    static {
        INSTANCE = new FlowShader();
    }
}