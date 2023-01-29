package com.kisman.cc.features.module.render.shader.shaders;

import com.kisman.cc.features.module.render.shader.GlowableShader;

public class CodeXShader extends GlowableShader {
    public static CodeXShader CodeX_SHADER;

    public CodeXShader() {
        super("codex.frag");
    }

    static {
        CodeX_SHADER = new CodeXShader();
    }
}
