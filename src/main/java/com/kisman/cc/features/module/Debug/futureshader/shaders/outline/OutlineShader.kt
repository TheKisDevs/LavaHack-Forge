package com.kisman.cc.features.module.Debug.futureshader.shaders.outline

import com.kisman.cc.features.module.Debug.futureshader.FramebufferShader
import org.lwjgl.opengl.GL20

/**
 * @author _kisman_
 * @since 11:31 of 10.06.2022
 */
object OutlineShader : FramebufferShader("outline", "outline") {
    var opacity = 1f
    var width = 1f
    var quality = 1f

    override fun setupUniforms() {
        setupUniform("texture")
        setupUniform("texelSize")
        setupUniform("opacityModifier")
    }

    override fun updateUniforms() {
        GL20.glUniform1i(getUniform("texture"), 0)
        GL20.glUniform2f(getUniform("texelSize"), 1 / mc.displayWidth * width * quality, 1 / mc.displayHeight * width * quality)
        GL20.glUniform1f(getUniform("opacityModifier"), opacity)
    }
}