package com.kisman.cc.settings.util.shadercharmsrewrite

import com.kisman.cc.settings.util.ShaderCharmsRewritePattern
import com.kisman.cc.util.render.shader.framebuffer.FramebufferShader

/**
 * @author _kisman_
 * @since 13:03 of 18.08.2022
 */
abstract class ShaderCharmsRewriteFramebufferShader(
    private val pattern : ShaderCharmsRewritePattern,
    fragmentShader: String
) : FramebufferShader(
    fragmentShader
) {

    override fun setupUniforms() {

    }

    override fun updateUniforms() {

    }
}