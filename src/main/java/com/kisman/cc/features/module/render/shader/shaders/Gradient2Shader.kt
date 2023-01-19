package com.kisman.cc.features.module.render.shader.shaders

import com.kisman.cc.features.module.render.shader.FramebufferShader
import com.kisman.cc.util.render.Rendering
import org.lwjgl.opengl.GL20

/**
 * @author _kisman_
 * @since 20:24 of 17.01.2023
 */
object Gradient2Shader : FramebufferShader(
    "gradient2.frag"
) {
    @JvmField var time = 1f

    @JvmField var rgba = Rendering.DUMMY_COLOR
    @JvmField var rgba1 = Rendering.DUMMY_COLOR
    @JvmField var step = 50f
    @JvmField var speed = 50f
    @JvmField var mix = 1f
//    @JvmField var customAlpha = false
    @JvmField var alpha = 1f;


    override fun setupUniforms() {
        /*setupUniforms(
            "texture",
            "rgb",
            "rgb1",
            "step",
            "offset",
            "mix"
        )*/

        setupUniform("texture")
        setupUniform("rgb")
        setupUniform("rgb1")
        setupUniform("step")
        setupUniform("offset")
        setupUniform("mix")

    }

    override fun updateUniforms() {
        GL20.glUniform1i(getUniform("texture"), 0)
        GL20.glUniform3f(getUniform("rgb"), rgba.r1, rgba.g1, rgba.b1/*, rgba.a1*/)
        GL20.glUniform3f(getUniform("rgb1"), rgba1.r1, rgba1.g1, rgba1.b1/*, rgba1.a1*/)
        GL20.glUniform1f(getUniform("step"), 300 * step)
        GL20.glUniform1f(getUniform("offset"), (System.currentTimeMillis().toDouble() * speed.toDouble() % (mc.displayWidth * mc.displayHeight) / 10.0f).toFloat())
        GL20.glUniform1f(getUniform("mix"), mix)
//        GL20.glUniform1i(getUniform("customAlpha"), if(customAlpha) 1 else 0)
//        GL20.glUniform1f(getUniform("alpha"), alpha)
    }
}