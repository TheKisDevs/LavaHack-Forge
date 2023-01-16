package com.kisman.cc.util.render.shader

import com.kisman.cc.features.module.render.shader.FramebufferShader
import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.enums.Shaders
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.shader.Framebuffer
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20


/**
 * @author _kisman_
 * @since 11:17 of 14.01.2023
 */

fun startShader(
    shader : Shaders,
    uniforms : () -> Unit,
    ticks : Float
) {
    GlStateManager.matrixMode(5889)
    GlStateManager.pushMatrix()
    GlStateManager.matrixMode(5888)
    GlStateManager.pushMatrix()

    uniforms()

    shader.buffer.startDraw(ticks)
}

fun endShader(
    shader : Shaders
) {
    shader.buffer.stopDraw()

    GlStateManager.color(1f, 1f, 1f)
    GlStateManager.matrixMode(5889)
    GlStateManager.popMatrix()
    GlStateManager.matrixMode(5888)
    GlStateManager.popMatrix()
}

fun startShader2(
    shader : Shaders,
    uniforms : () -> Unit
) {
    GlStateManager.matrixMode(5889)
    GlStateManager.pushMatrix()
    GlStateManager.matrixMode(5888)
    GlStateManager.pushMatrix()

    uniforms()

    shader.buffer.startDraw2()
}

fun endShader2(
    shader : Shaders
) {
    shader.buffer.stopDraw2()

    GlStateManager.color(1f, 1f, 1f)
    GlStateManager.matrixMode(5889)
    GlStateManager.popMatrix()
    GlStateManager.matrixMode(5888)
    GlStateManager.popMatrix()
}

fun startShader3(
    shader : Shaders,
    uniforms : () -> Unit
) {
    GlStateManager.enableBlend()
    GlStateManager.color(1f, 1f, 1f, 1f)
    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)

    val buffer = shader.buffer.setupFrameBuffer(FramebufferShader.framebuffer)

    mc.framebuffer.bindFramebuffer(true)

    GL20.glUseProgram(shader.buffer.program)

    uniforms()

    GL11.glBindTexture(GL11.GL_TEXTURE_2D, buffer.framebufferTexture)
}

fun endShader3() {
    GL20.glUseProgram(0)

    GlStateManager.color(1f, 1f, 1f, 1f)
    GlStateManager.bindTexture(0)
    GL11.glEnable(GL11.GL_BLEND)
}

fun createFrameBuffer(
    framebuffer : Framebuffer?
) : Framebuffer {
    if (framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
        framebuffer?.deleteFramebuffer()
        return Framebuffer(mc.displayWidth, mc.displayHeight, true)
    }
    return framebuffer
}