package com.kisman.cc.features.module.Debug

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Debug.futureshader.shaders.outline.OutlineShader
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.math.MathUtil
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.Entity
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.*

/**
 * @author _kisman_
 * @since 11:11 of 10.06.2022
 */
class FutureShader : Module("FutureShader", Category.DEBUG) {
    private val opacityOutline = register(Setting("Opacity Outline", this, 1.0, 0.0, 1.0, false))
    private val width = register(Setting("Width", this, 1.0, 0.1, 3.0, false))
    private val quality = register(Setting("Quality", this, 1.0, 0.1, 3.0, false))

    @SubscribeEvent fun onRenderWorld(event : RenderWorldLastEvent) {
        GlStateManager.matrixMode(5889)
        GlStateManager.pushMatrix()
        GlStateManager.matrixMode(5888)
        GlStateManager.pushMatrix()

        OutlineShader.quality = quality.valFloat
        OutlineShader.width = width.valFloat
        OutlineShader.opacity = opacityOutline.valFloat

        OutlineShader.startDraw(event.partialTicks)

        for(entity in mc.world.loadedEntityList) {
            if(entity == mc.player) continue
            val vector = MathUtil.getInterpolatedRenderPos(entity, event.partialTicks)
            Objects.requireNonNull(mc.getRenderManager().getEntityRenderObject<Entity>(entity))!!.doRender(entity, vector.x, vector.y, vector.z, entity.rotationYaw, event.partialTicks)

        }

        OutlineShader.stopDraw()

        GlStateManager.color(1f, 1f, 1f)
        GlStateManager.matrixMode(5889)
        GlStateManager.popMatrix()
        GlStateManager.matrixMode(5888)
        GlStateManager.popMatrix()
    }
}