package com.kisman.cc.features.schematica.schematica.client.renderer.shader

import com.kisman.cc.features.module.render.shader.FramebufferShader
import org.lwjgl.opengl.GL20

/**
 * @author _kisman_
 * @since 8:53 of 06.03.2023
 */
class ShaderAlpha : FramebufferShader(
    "alpha.frag"
) {
    @JvmField var alpha = 1.0f

    override fun setupUniforms() {
        setupUniform("alpha_multiplier")
    }

    override fun updateUniforms() {
        GL20.glUniform1f(getUniform("alpha_multiplier"), alpha)
    }
}