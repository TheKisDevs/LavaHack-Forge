package com.kisman.cc.util.render.pattern

import com.kisman.cc.features.module.combat.autorer.AutoRerUtil
import com.kisman.cc.settings.util.RenderingRewritePattern
import com.kisman.cc.settings.util.SlideRenderingRewritePattern
import com.kisman.cc.util.Colour
import com.kisman.cc.util.enums.dynamic.EasingEnum
import com.kisman.cc.util.math.toDelta
import com.kisman.cc.util.render.objects.world.TextOnBlockObject
import net.minecraft.client.renderer.entity.Render
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
    @JvmField var scale = 0.0

    open fun reset() {
        lastBlockPos = null
        prevPos = null
        currentPos = null
        lastRenderPos = null
        lastUpdateTime = 0L
        startTime = 0L
        scale = 0.0
    }

    open fun handleRenderWorld(
        movingLength : Float,
        fadeLength : Float,
        renderer : RenderingRewritePattern,
        pos : BlockPos?,
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

    open fun handleRenderWorld(
        renderer : SlideRenderingRewritePattern,
        pos : BlockPos?,
        text : String?
    ) {
        handleRenderWorld(
            renderer.movingLength.valFloat,
            renderer.fadeLength.valFloat,
            renderer,
            pos,
            text
        )
    }

    open fun renderWorld(
        movingLength : Float,
        fadeLength : Float,
        renderer : RenderingRewritePattern,
        aabbModifier : (AxisAlignedBB) -> AxisAlignedBB,
        text : String?
    ) {
        prevPos?.let { prevPos ->
            (currentPos ?: prevPos).let { currentPos ->
                scale = scale(fadeLength, renderer)

                val multiplier = multiplier(movingLength, renderer)

                val renderPos = prevPos.add(currentPos.subtract(prevPos).scale(multiplier))

                renderer.draw(aabbModifier(toRenderBox(renderPos, scale)))

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

    open fun renderWorld(
        movingLength : Float,
        fadeLength : Float,
        renderer : RenderingRewritePattern,
        text : String?
    ) {
        renderWorld(
            movingLength,
            fadeLength,
            renderer,
            { it },
            text
        )
    }

    protected fun multiplier(
        movingLength : Float,
        renderer : RenderingRewritePattern
    ) : Double = if(movingLength != 0f) {
        if(renderer is SlideRenderingRewritePattern) {
            renderer.movingOutEasing.getValElement().inc(toDelta(lastUpdateTime, movingLength))
        } else {
            EasingEnum.Easing.OutQuart.inc(toDelta(lastUpdateTime, movingLength))
        }
    } else {
        1.0
    }

    protected fun scale(
        fadeLength : Float,
        renderer : RenderingRewritePattern
    ) : Double = if(fadeLength != 0f) {
        if(renderer is SlideRenderingRewritePattern) {
            if(this.currentPos != null) {
                renderer.fadeOutEasing.getValElement().inc(toDelta(startTime, fadeLength))
            } else {
                renderer.fadeInEasing.getValElement().dec(toDelta(startTime, fadeLength))
            }
        } else {
            if (this.currentPos != null) {
                EasingEnum.Easing.OutCubic.inc(toDelta(startTime, fadeLength))
            } else {
                EasingEnum.Easing.InCubic.dec(toDelta(startTime, fadeLength))
            }
        }
    } else {
        if(this.currentPos != null) {
            1.0
        } else {
            0.0
        }
    }

    protected open fun toRenderBox(
        vec3d : Vec3d,
        scale : Double
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
            if(pos != lastBlockPos) {
                currentPos = if (pos != null) AutoRerUtil.toVec3dCenter(pos) else null
                prevPos = lastRenderPos ?: currentPos
                lastUpdateTime = System.currentTimeMillis()
                if (lastBlockPos == null) {
                    startTime = System.currentTimeMillis()
                }

                lastBlockPos = pos
            }
    }
}