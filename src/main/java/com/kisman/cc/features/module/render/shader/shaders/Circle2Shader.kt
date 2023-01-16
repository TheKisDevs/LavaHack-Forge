package com.kisman.cc.features.module.render.shader.shaders

import com.kisman.cc.features.module.render.shader.FramebufferShader
import com.kisman.cc.util.render.Rendering
import org.lwjgl.opengl.GL20

/**
 * @author _kisman_
 * @since 16:03 of 14.01.2023
 */
object Circle2Shader : FramebufferShader(
    "circle3.frag"
) {
    @JvmField var time = 1f

    @JvmField var rgba = Rendering.DUMMY_COLOR
    @JvmField var rgba1 = Rendering.DUMMY_COLOR
    @JvmField var step = 50f
    @JvmField var speed = 50f
    @JvmField var mix = 1f
    @JvmField var customAlpha = false
    @JvmField var alpha = 1f;


    override fun setupUniforms() {
        setupUniforms(
            "texture",
            "rgba",
            "rgba1",
            "step",
            "offset",
            "mix",
            "customAlpha",
            "alpha"
        )
    }

    override fun updateUniforms() {
        GL20.glUniform1i(getUniform("texture"), 0)
        GL20.glUniform4f(getUniform("rgba"), rgba.r1, rgba.g1, rgba.b1, rgba.a1)
        GL20.glUniform4f(getUniform("rgba1"), rgba1.r1, rgba1.g1, rgba1.b1, rgba1.a1)
        GL20.glUniform1f(getUniform("mix"), mix)
        GL20.glUniform1i(getUniform("customAlpha"), if(customAlpha) 1 else 0)
        GL20.glUniform1f(getUniform("alpha"), alpha)
        GL20.glUniform1f(getUniform("step"), 300 * step)
        GL20.glUniform1f(getUniform("offset"), (System.currentTimeMillis().toDouble() * speed.toDouble() % (mc.displayWidth * mc.displayHeight) / 10.0f).toFloat())
    }
}