package com.kisman.cc.settings.util.shaders

import com.kisman.cc.settings.util.ShadersRendererPattern
import com.kisman.cc.util.render.shader.framebuffer.FramebufferShader

/**
 * @author _kisman_
 * @since 13:03 of 18.08.2022
 */
abstract class ShaderCharmsRewriteFramebufferShader(
    private val pattern : ShadersRendererPattern,
    fragmentShader: String
) : FramebufferShader(
    fragmentShader
) {

    override fun setupUniforms() {

    }

    override fun updateUniforms() {

    }
}