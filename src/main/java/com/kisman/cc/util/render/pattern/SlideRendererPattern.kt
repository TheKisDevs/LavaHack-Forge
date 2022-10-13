package com.kisman.cc.util.render.pattern

import com.kisman.cc.features.module.combat.autorer.AutoRerUtil
import com.kisman.cc.features.module.combat.autorer.util.Easing
import com.kisman.cc.settings.util.RenderingRewritePattern
import com.kisman.cc.settings.util.SlideRenderingRewritePattern
import com.kisman.cc.util.Colour
import com.kisman.cc.util.enums.dynamic.EasingEnum
import com.kisman.cc.util.math.toDelta
import com.kisman.cc.util.render.objects.world.TextOnBlockObject
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

/**
 * @author _kisman_
 * @since 19:06 of 11.10.2022
 */
open class SlideRendererPattern {
    @JvmField var lastBlockPos : BlockPos? = null
    @JvmField var prevPos : Vec3d? = null
    @JvmField var currentPos : Vec3d? = null
    @JvmField var lastRenderPos : Vec3d? = null
    @JvmField var lastUpdateTime = 0L
    @JvmField var startTime = 0L
    @JvmField var scale = 0.0f
    @JvmField var lastSelfDamage = 0.0f
    @JvmField var lastTargetDamage = 0.0f

    fun reset() {
        lastBlockPos = null
        prevPos = null
        currentPos = null
        lastRenderPos = null
        lastUpdateTime = 0L
        startTime = 0L
        scale = 0.0f
        lastSelfDamage = 0.0f
        lastTargetDamage = 0.0f
    }

    open fun handleRenderWorld(
        movingLength : Float,
        fadeLength : Float,
        renderer : RenderingRewritePattern,
        pos : BlockPos,
        text : String?
    ) {
        update(pos)
        renderWorld(
            movingLength,
            fadeLength,
            renderer,
            text
        )
    }

    fun renderWorld(
        movingLength : Float,
        fadeLength : Float,
        renderer : RenderingRewritePattern,
        text : String?
    ) {
        prevPos?.let { prevPos ->
            (currentPos ?: prevPos).let { currentPos ->
                scale = if(renderer is SlideRenderingRewritePattern) {
                    if(this.currentPos != null) {
                        renderer.fadeOutEasing.getValElement().inc(toDelta(startTime, fadeLength.toLong()).toDouble()).toFloat()
                    } else {
                        renderer.fadeInEasing.getValElement().dec(toDelta(startTime, fadeLength.toLong()).toDouble()).toFloat()
                    }
                } else {
                    if (this.currentPos != null) {
                        EasingEnum.Easing.OutCubic.inc(toDelta(startTime, fadeLength.toLong()).toDouble()).toFloat()
                    } else {
                        EasingEnum.Easing.InCubic.dec(toDelta(startTime, fadeLength.toLong()).toDouble()).toFloat()
                    }
                }

                val multiplier = if(renderer is SlideRenderingRewritePattern) {
                    renderer.movingOutEasing.getValElement().inc(toDelta(startTime, movingLength.toLong()).toDouble()).toFloat()
                } else {
                    EasingEnum.Easing.OutQuart.inc(toDelta(startTime, movingLength.toLong()).toDouble()).toFloat()
                }

                val renderPos = prevPos.add(currentPos.subtract(prevPos).scale(multiplier.toDouble()))

                renderer.draw(toRenderBox(renderPos, scale))

                lastRenderPos = renderPos

                if(text != null) {
                    TextOnBlockObject(
                        text,
                        BlockPos(currentPos),
                        Colour(255, 255, 255, (255.0f * scale).toInt())
                    )
                }
            }
        }
    }

    private fun toRenderBox(
        vec3d : Vec3d,
        scale : Float
    ) : AxisAlignedBB {
        val halfSize = 0.5 * scale
        return AxisAlignedBB(
            vec3d.x - halfSize + 0.5, vec3d.y - halfSize + 0.5, vec3d.z - halfSize + 0.5,
            vec3d.x + halfSize + 0.5, vec3d.y + halfSize + 0.5, vec3d.z + halfSize + 0.5
        )
    }

    open fun update(
        pos : BlockPos?
    ) {
        if (pos != lastBlockPos) {
            currentPos = if(pos != null) AutoRerUtil.toVec3dCenter(pos) else null
            prevPos = lastRenderPos ?: currentPos
            lastUpdateTime = System.currentTimeMillis()
            if (lastBlockPos == null) startTime = System.currentTimeMillis()

            lastBlockPos = pos
        }
    }
}