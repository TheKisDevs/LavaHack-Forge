package com.kisman.cc.util.render.shader.framebuffer

import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.sr
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.shader.Framebuffer
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20

/**
 * @author _kisman_
 * @since 20:32 of 17.08.2022
 */
abstract class FramebufferShader(
    fragmentShader : String
) : Shader(
    fragmentShader
) {
    private var entityShadows = false

    fun startDraw(ticks : Float) {
        enableAlpha()
        pushMatrix()
        pushAttrib()

        setupFramebuffer(framebuffer)
            .also { framebuffer = it }
            .framebufferClear()

        framebuffer?.bindFramebuffer(true)
        entityShadows = mc.gameSettings.entityShadows
        mc.gameSettings.entityShadows = false
        mc.entityRenderer.setupCameraTransform(ticks, 0)
    }

    fun stopDraw() {
        mc.gameSettings.entityShadows = entityShadows
        glEnable(3042)
        glBlendFunc(770, 771)
        mc.framebuffer.bindFramebuffer(true)
        mc.entityRenderer.disableLightmap()
        RenderHelper.disableStandardItemLighting()
        startShader()
        mc.entityRenderer.setupOverlayRendering()
        drawFramebuffer(framebuffer!!)
        stopShader()
        mc.entityRenderer.disableLightmap()
        popMatrix()
        popAttrib()
    }

    @Suppress("RemoveRedundantQualifierName")
    private fun drawFramebuffer(framebuffer : Framebuffer) {
        glBindTexture(3553, framebuffer.framebufferTexture)
        GL11.glBegin(7)

        glTexCoord2d(0.0, 1.0)
        glVertex2d(0.0, 0.0)

        glTexCoord2d(0.0, 0.0)
        glVertex2d(0.0, sr().scaledHeight_double)

        glTexCoord2d(1.0, 0.0)
        glVertex2d(sr().scaledWidth_double, sr().scaledHeight_double)

        glTexCoord2d(1.0, 1.0)
        glVertex2d(sr().scaledWidth_double, 0.0)

        GL11.glEnd()
        GL20.glUseProgram(0)
    }

    private fun setupFramebuffer(framebuffer : Framebuffer?) : Framebuffer {
        framebuffer?.deleteFramebuffer()

        return Framebuffer(
            mc.displayWidth,
            mc.displayHeight,
            true
        )
    }

    companion object {
        var framebuffer : Framebuffer? = null
    }
}