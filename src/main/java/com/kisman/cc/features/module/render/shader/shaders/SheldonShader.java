package com.kisman.cc.features.module.render.shader.shaders;

import com.kisman.cc.features.module.render.shader.GlowableShader;

public class SheldonShader extends GlowableShader {
    public static SheldonShader SHELDON_SHADER;

    public SheldonShader() {
        super("sheldon.frag");
    }

    static {
        SHELDON_SHADER = new SheldonShader();
    }
}
