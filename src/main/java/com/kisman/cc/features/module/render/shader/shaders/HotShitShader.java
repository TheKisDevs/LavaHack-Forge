package com.kisman.cc.features.module.render.shader.shaders;

import com.kisman.cc.features.module.render.shader.GlowableShader;

public class HotShitShader extends GlowableShader {
    public static HotShitShader HotShit_SHADER;

    public HotShitShader() {
        super("hotshit.frag");
    }

    static {
        HotShit_SHADER = new HotShitShader();
    }
}
