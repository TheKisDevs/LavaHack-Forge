package com.kisman.cc.features.module.render.shader.shaders

import com.kisman.cc.features.module.render.ShaderCharms
import com.kisman.cc.features.module.render.shader.shaders.troll.ShaderHelper
import com.kisman.cc.util.entity.EntityUtil
import com.kisman.cc.util.math.MathUtil
import com.kisman.cc.util.render.GlStateUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.client.shader.Framebuffer
import net.minecraft.entity.Entity
import org.lwjgl.opengl.GL11

/**
 * @author _kisman_
 * @since 13:13 of 15.05.2022
 */
object InertiaOutlineShader {
    private val mc = Minecraft.getMinecraft()

    fun drawShader(shaderHelper : ShaderHelper, frameBufferFinal : Framebuffer, ticks : Float) {
        // Push matrix
        GlStateUtils.pushMatrixAll()

        GlStateUtils.blend(true)
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)

        shaderHelper.shader!!.render(ticks)

        // Re-enable blend because shader rendering will disable it at the end
        GlStateUtils.blend(true)
        GlStateUtils.depth(false)

        // Draw it on the main frame buffer
        mc.framebuffer.bindFramebuffer(false)
        frameBufferFinal!!.framebufferRenderExt(mc.displayWidth, mc.displayHeight, false)

        // Revert states
        GlStateUtils.blend(true)
        GlStateUtils.depth(true)
//        GlStateUtils.texture2d(false)
//        GlStateManager.depthMask(false)

        // Revert matrix
        GlStateUtils.popMatrixAll()
    }

    fun drawEntities(partialTicks: Float, range: Float) {
        GlStateUtils.texture2d(true)
        GlStateUtils.alpha(true)
        GlStateUtils.depth(true)
        GlStateManager.depthMask(true)

        val camera = Frustum()
        val viewEntity = mc.renderViewEntity ?: mc.player
        val partialTicksD = partialTicks.toDouble()
        val x = MathUtil.lerp(viewEntity.lastTickPosX, viewEntity.posX, partialTicksD)
        val y = MathUtil.lerp(viewEntity.lastTickPosY, viewEntity.posY, partialTicksD)
        val z = MathUtil.lerp(viewEntity.lastTickPosZ, viewEntity.posZ, partialTicksD)

        camera.setPosition(x, y, z)

        for(entity in mc.world.loadedEntityList) {
            if(mc.player.getDistance(entity) > range) continue
            if(!ShaderCharms.instance.entityTypeCheck(entity)) continue

            val renderer = mc.renderManager.getEntityRenderObject<Entity>(entity) ?: continue

            if(!renderer.shouldRender(entity, camera, x, y, z)) continue

            val yaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks
            val pos = EntityUtil.getInterpolatedPos(entity, partialTicks).subtract(mc.renderManager.renderPosX, mc.renderManager.renderPosY, mc.renderManager.renderPosZ)

            renderer.setRenderOutlines(true)
            renderer.doRender(entity, pos.x, pos.y, pos.z, yaw, partialTicks)
        }

        GlStateUtils.texture2d(false)
        GlStateUtils.alpha(false)
    }

    fun setupUniforms() {}
    fun updateUniforms(shaderHelper: ShaderHelper) {}
}