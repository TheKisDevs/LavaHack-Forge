package com.kisman.cc.features.module.render.shader.shaders;

import com.kisman.cc.features.module.render.shader.GlowableShader;

public class GoldenShader extends GlowableShader {
    public static GoldenShader GOLDEN_SHADER;

    public GoldenShader() {
        super("golden.frag");
    }

    static {
        GOLDEN_SHADER = new GoldenShader();
    }
}
