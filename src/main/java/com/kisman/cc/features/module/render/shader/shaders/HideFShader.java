package com.kisman.cc.features.module.render.shader.shaders;

import com.kisman.cc.features.module.render.shader.GlowableShader;

public class HideFShader extends GlowableShader {
    public static HideFShader HideF_SHADER;

    public HideFShader() {
        super("hidef.frag");
    }

    static {
        HideF_SHADER = new HideFShader();
    }
}
