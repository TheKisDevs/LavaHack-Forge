package com.kisman.cc.features.module.render.shader.shaders

import com.kisman.cc.features.module.render.shader.FramebufferShader
import com.kisman.cc.util.render.Rendering
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.opengl.GL20

/**
 * @author _kisman_
 * @since 12:36 of 04.01.2023
 */
object CircleShader : FramebufferShader(
    "circle2.frag"
) {
    @JvmField var time = 1f

    @JvmField var color1 = Rendering.DUMMY_COLOR
    @JvmField var color2 = Rendering.DUMMY_COLOR
    @JvmField var filledColor = Rendering.DUMMY_COLOR
    @JvmField var outlineColor = Rendering.DUMMY_COLOR

    @JvmField var customAlpha = false
    @JvmField var rainbow = false
    @JvmField var circle = false
    @JvmField var filled = false
    @JvmField var glow = false
    @JvmField var outline = false
    @JvmField var fadeOutline = false

    @JvmField var mix = 1f
    @JvmField var rainbowAlpha = 1f
    @JvmField var circleRadius = 5f
    @JvmField var glowRadius = 1f
    @JvmField var outlineRadius = 1f
    @JvmField var quality = 1f

    override fun setupUniforms() {
        setupUniform("time")
        setupUniform("resolution")
        setupUniform("divider")
        setupUniform("maxSample")
        setupUniform("texelSize")
        setupUniform("color1")
        setupUniform("color2")
        setupUniform("filledColor")
        setupUniform("outlineColor")
        setupUniform("customAlpha")
        setupUniform("mix")
        setupUniform("filled")
        setupUniform("rainbow")
        setupUniform("rainbowAlpha")
        setupUniform("circle")
        setupUniform("circleRadius")
        setupUniform("glow")
        setupUniform("glowRadius")
        setupUniform("outline")
        setupUniform("fadeOutline")
        setupUniform("outlineRadius")
    }

    override fun updateUniforms() {
        GL20.glUniform1f(getUniform("time"), time)
        GL20.glUniform2f(getUniform("resolution"), ScaledResolution(mc).scaledWidth.toFloat(), ScaledResolution(mc).scaledHeight.toFloat())
        GL20.glUniform1f(getUniform("divider"), 140f);
        GL20.glUniform1f(getUniform("maxSample"), 10f);
        GL20.glUniform2f(getUniform("texelSize"), 1f / mc.displayWidth * (glowRadius * quality), 1f / mc.displayHeight * (glowRadius * quality))
        GL20.glUniform4f(getUniform("color1"), color1.r1, color1.g1, color1.b1, color1.a1)
        GL20.glUniform4f(getUniform("color2"), color2.r1, color2.g1, color2.b1, color2.a1)
        GL20.glUniform4f(getUniform("filledColor"), filledColor.r1, filledColor.g1, filledColor.b1, filledColor.a1)
        GL20.glUniform4f(getUniform("outlineColor"), outlineColor.r1, outlineColor.g1, outlineColor.b1, outlineColor.a1)
        GL20.glUniform1i(getUniform("customAlpha"), if(customAlpha) 1 else 0)
        GL20.glUniform1f(getUniform("mix"), mix)
        GL20.glUniform1i(getUniform("filled"), if(filled) 1 else 0)
        GL20.glUniform1i(getUniform("rainbow"), if(rainbow) 1 else 0)
        GL20.glUniform1f(getUniform("rainbowAlpha"), rainbowAlpha)
        GL20.glUniform1i(getUniform("circle"), if(circle) 1 else 0)
        GL20.glUniform1f(getUniform("circleRadius"), circleRadius)
        GL20.glUniform1i(getUniform("glow"), if(glow) 1 else 0)
        GL20.glUniform1f(getUniform("glowRadius"), glowRadius)
        GL20.glUniform1i(getUniform("outline"), if(outline) 1 else 0)
        GL20.glUniform1i(getUniform("fadeOutline"), if(fadeOutline) 1 else 0)
        GL20.glUniform1f(getUniform("outlineRadius"), outlineRadius)

        time += animationSpeed
    }
}