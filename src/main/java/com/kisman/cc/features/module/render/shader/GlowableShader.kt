package com.kisman.cc.features.module.render.shader

import com.kisman.cc.util.sr
import org.lwjgl.opengl.GL20

/**
 * @author _kisman_
 * @since 13:14 of 29.01.2023
 */
open class GlowableShader(
    shader : String
) : FramebufferShader(
    shader
) {
    var time = 1f

    @JvmField var radius = 0f
    @JvmField var quality = 0f

    override fun setupUniforms() {
        setupUniforms("resolution", "time", "radius", "quality")
    }

    override fun updateUniforms() {
        GL20.glUniform2f(getUniform("resolution"), sr().scaledWidth.toFloat(), sr().scaledHeight.toFloat())
        GL20.glUniform1f(getUniform("time"), time)
        GL20.glUniform1f(getUniform("radius"), radius)
        GL20.glUniform1f(getUniform("quality"), quality)

        time += 0.01f * animationSpeed
    }
}