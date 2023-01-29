package com.kisman.cc.features.module.render.shader.shaders;

import com.kisman.cc.features.module.render.shader.GlowableShader;

public class TechnoShader extends GlowableShader {
    public static TechnoShader TECHNO_SHADER;

    public TechnoShader() {
        super("techno.frag");
    }

    static {
        TECHNO_SHADER = new TechnoShader();
    }
}
