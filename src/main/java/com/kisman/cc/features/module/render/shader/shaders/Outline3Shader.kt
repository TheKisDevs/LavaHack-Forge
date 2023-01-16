package com.kisman.cc.features.module.render.shader.shaders

import com.kisman.cc.features.module.render.shader.FramebufferShader
import com.kisman.cc.util.render.Rendering
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.opengl.GL20

/**
 * @author _kisman_
 * @since 16:14 of 14.01.2023
 */
object Outline3Shader : FramebufferShader(
    "outline2.frag"
) {
    @JvmField var time = 1f

    @JvmField var outlineColor = Rendering.DUMMY_COLOR
    @JvmField var filledColor = Rendering.DUMMY_COLOR
    @JvmField var filledMix = 1f
    @JvmField var radius = 50f
    @JvmField var ratio = 50f


    override fun setupUniforms() {
        setupUniforms(
            "texture",
            "resolution",
            "outlineColor",
            "filledColor",
            "filledMix",
            "radius",
            "ratio"
        )
    }

    override fun updateUniforms() {
        GL20.glUniform1i(getUniform("texture"), 0)
        GL20.glUniform2f(getUniform("resolution"), ScaledResolution(Minecraft.getMinecraft()).scaledWidth.toFloat(), ScaledResolution(Minecraft.getMinecraft()).scaledHeight.toFloat())
        GL20.glUniform4f(getUniform("outlineColor"), outlineColor.r1, outlineColor.g1, outlineColor.b1, outlineColor.a1)
        GL20.glUniform3f(getUniform("filledColor"), filledColor.r1, filledColor.g1, filledColor.b1)
        GL20.glUniform1f(getUniform("filledMix"), filledMix)
        GL20.glUniform1f(getUniform("radius"), radius)
        GL20.glUniform1f(getUniform("ratio"), ratio)
    }
}