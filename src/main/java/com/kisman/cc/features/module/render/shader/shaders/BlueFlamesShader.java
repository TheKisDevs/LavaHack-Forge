package com.kisman.cc.features.module.render.shader.shaders;

import com.kisman.cc.features.module.render.shader.GlowableShader;

public class BlueFlamesShader extends GlowableShader {
    public static BlueFlamesShader BlueFlames_SHADER;

    public BlueFlamesShader() {
        super("blueflames.frag");
    }

    static {
        BlueFlames_SHADER = new BlueFlamesShader();
    }
}
