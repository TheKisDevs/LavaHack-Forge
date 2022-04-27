package com.kisman.cc.util.render.objects

import com.kisman.cc.util.Colour
import com.kisman.cc.util.enums.Object3dTypes
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL32

abstract class Abstract3dObject {
    abstract val type: Object3dTypes
    abstract val color: Colour
    abstract fun draw(ticks: Float)

    fun prepare(depth: Boolean, alpha: Boolean) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS)
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST)
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_ONE)
        GlStateManager.shadeModel(GL11.GL_SMOOTH)
        GlStateManager.depthMask(false)
        GlStateManager.enableBlend()
        if(!depth) GlStateManager.disableDepth()
        GlStateManager.disableTexture2D()
        GlStateManager.disableLighting()
        GlStateManager.disableCull()
        if(alpha) GlStateManager.enableAlpha()
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glEnable(GL32.GL_DEPTH_CLAMP)
    }

    fun release(alpha: Boolean) {
        GL11.glDisable(GL32.GL_DEPTH_CLAMP)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        if(alpha) GlStateManager.enableAlpha()
        GlStateManager.enableCull()
        GlStateManager.enableLighting()
        GlStateManager.enableTexture2D()
        GlStateManager.enableDepth()
        GlStateManager.disableBlend()
        GlStateManager.depthMask(true)
        GlStateManager.glLineWidth(1.0f)
        GlStateManager.shadeModel(GL11.GL_FLAT)
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_DONT_CARE)
        GL11.glPopAttrib()
    }
}