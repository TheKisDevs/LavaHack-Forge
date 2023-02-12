package com.kisman.cc.features.module.render.shader.shaders;

import com.kisman.cc.features.module.render.shader.GlowableShader;

public class CrazyShader extends GlowableShader {
    public static CrazyShader CRAZY_SHADER;

    public CrazyShader() {
        super("crazy.frag");
    }

    static {
        CRAZY_SHADER = new CrazyShader();
    }
}
