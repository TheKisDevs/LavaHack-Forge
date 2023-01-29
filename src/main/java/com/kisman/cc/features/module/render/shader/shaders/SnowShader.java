package com.kisman.cc.features.module.render.shader.shaders;

import com.kisman.cc.features.module.render.shader.GlowableShader;

public class SnowShader extends GlowableShader {
    public static SnowShader SNOW_SHADER;

    public SnowShader() {
        super("snow.frag");
    }

    static {
        SNOW_SHADER = new SnowShader();
    }
}
