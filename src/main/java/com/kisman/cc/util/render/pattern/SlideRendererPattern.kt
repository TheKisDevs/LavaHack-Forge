package com.kisman.cc.util.render.pattern

import com.kisman.cc.features.module.combat.autorer.AutoRerUtil
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
    @JvmField var scale = 0.0
    @JvmField val processedPossesList = HashMap<BlockPos, Long>()

    open fun reset() {
        lastBlockPos = null
        prevPos = null
        currentPos = null
        lastRenderPos = null
        lastUpdateTime = 0L
        startTime = 0L
        scale = 0.0
        processedPossesList.clear()
    }

    open fun handleRenderWorld(
        movingLength : Float,
        fadeLength : Float,
        alphaFadeLength : Float,
        renderer : RenderingRewritePattern,
        pos : BlockPos?,
        text : String?
    ) {
        update(pos)
        renderWorld(
            movingLength,
            fadeLength,
            alphaFadeLength,
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
            renderer.alphaFadeLength.valFloat,
            renderer,
            pos,
            text
        )
    }

    open fun renderWorld(
        movingLength : Float,
        fadeLength : Float,
        alphaFadeLength : Float,
        renderer : RenderingRewritePattern,
        aabbModifier : (AxisAlignedBB) -> AxisAlignedBB,
        text : String?
    ) {
        var multiplier = -1.0

        prevPos?.let { prevPos ->
            (currentPos ?: prevPos).let { currentPos ->
                scale = scale(fadeLength, renderer)

                multiplier = multiplier(movingLength, renderer)

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

        if(alphaFadeLength != 0f) {
            val keysToRemove = ArrayList<BlockPos>()

            for(pos in processedPossesList) {
                if(multiplier == 0.0 && pos.key == currentPos) {
                    keysToRemove.add(pos.key)
                    println("renders current pos")
                    continue
                }

                val alphaCoeff = alpha(
                    alphaFadeLength,
                    renderer,
                    pos.value
                )

                println("alpha coeff is $alphaCoeff")

                if(alphaCoeff == 0.0) {
                    keysToRemove.add(pos.key)
                    println("removes some pos cuz alpha coeff is 0")
                    continue
                }

                val alpha1 = (alphaCoeff * renderer.color1.colour.alpha).toInt()
                val alpha2 = (alphaCoeff * renderer.color2.colour.alpha).toInt()

                println("alpha coeff that was casted to int $alpha1 for color 1")
                println("alpha coeff that was casted to int $alpha2 for color 2")

                renderer.draw(
                    pos.key,
                    renderer.color1.colour.withAlpha(alpha1),
                    renderer.color2.colour.withAlpha(alpha2)
                )
            }

            for(key in keysToRemove) {
                processedPossesList.remove(key)
            }
        }
    }

    open fun renderWorld(
        movingLength : Float,
        fadeLength : Float,
        alphaFadeLength : Float,
        renderer : RenderingRewritePattern,
        text : String?
    ) {
        renderWorld(
            movingLength,
            fadeLength,
            alphaFadeLength,
            renderer,
            { it },
            text
        )
    }

    protected fun alpha(
        alphaFadeLength: Float,
        renderer : RenderingRewritePattern,
        time : Long
    ) : Double = if(alphaFadeLength != 0f) {
        if(renderer is SlideRenderingRewritePattern) {
            renderer.alphaFadeEasing.valEnum.inc(toDelta(time, alphaFadeLength))
        } else {
            EasingEnum.Easing.OutQuart.dec(toDelta(time, alphaFadeLength))
        }
    } else {
        1.0
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
        if(pos != null) {
            processedPossesList[pos] = System.currentTimeMillis()
        }

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