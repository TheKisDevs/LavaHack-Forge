package com.kisman.cc.features.module.render.shader.shaders;

import com.kisman.cc.features.module.render.shader.GlowableShader;

public class SmokyShader extends GlowableShader {
    public static SmokyShader SMOKY_SHADER;

    public SmokyShader() {
        super("smoky.frag");
    }

    static {
        SMOKY_SHADER = new SmokyShader();
    }
}
