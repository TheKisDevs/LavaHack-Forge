package com.kisman.cc.util.render.pattern

import com.kisman.cc.settings.util.RenderingRewritePattern
import com.kisman.cc.settings.util.SlideRenderingRewritePattern
import com.kisman.cc.util.Colour
import com.kisman.cc.util.enums.dynamic.EasingEnum
import com.kisman.cc.util.math.toDelta
import com.kisman.cc.util.math.vectors.i2d
import com.kisman.cc.util.math.vectors.xyz.ColorableSlidePos
import com.kisman.cc.util.render.Rendering
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
        update(pos, renderer)
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
        aabbModifier : (AxisAlignedBB) -> AxisAlignedBB,
        pos : BlockPos?,
        text : String?
    ) {
        update(pos, renderer)
        renderWorld(
            renderer.movingLength.valFloat,
            renderer.fadeLength.valFloat,
            renderer.alphaFadeLength.valFloat,
            renderer,
            aabbModifier,
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
        val multiplier = multiplier(movingLength, renderer)

        prevPos?.let { prevPos ->
            (currentPos ?: prevPos).let { currentPos ->
                scale = scale(fadeLength, renderer)

                val renderPos = prevPos.add(currentPos.subtract(prevPos).scale(multiplier))

                renderer.draw(aabbModifier(toRenderBox(renderPos, scale)))

                lastRenderPos = renderPos

                if(text != null) {
                    Rendering.TextRendering.drawText(
                        BlockPos(currentPos),
                        text,
                        Colour(255, 255, 255, (255.0f * scale).toInt()).rgb
                    )
                }
            }
        }

        if(alphaFadeLength != 0f) {
            val keysToRemove = ArrayList<BlockPos>()

            for(entry in processedPossesList) {
                val pos = entry.key
                val time = entry.value

                if(multiplier == 0.0 && pos == currentPos) {
                    keysToRemove.add(pos)
                    continue
                }

                val alphaCoeff = alpha(
                    alphaFadeLength,
                    renderer,
                    time
                )

                if(alphaCoeff == 0.0) {
                    keysToRemove.add(pos)
                    continue
                }

                renderer.draw(
                    pos,
                    if(pos is ColorableSlidePos) pos.colour1 else renderer.getFilledColor1(),
                    if(pos is ColorableSlidePos) pos.colour2 else renderer.getFilledColor2(),
                    if(pos is ColorableSlidePos) pos.colour3 else renderer.getOutlineColor1(),
                    if(pos is ColorableSlidePos) pos.colour4 else renderer.getOutlineColor2(),
                    if(pos is ColorableSlidePos) pos.colour5 else renderer.getWireColor1(),
                    if(pos is ColorableSlidePos) pos.colour6 else renderer.getWireColor2(),
                    alphaCoeff
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
        alphaFadeLength : Float,
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

    fun updatePosses(
        pos : BlockPos?,
        renderer : RenderingRewritePattern
    ) : ColorableSlidePos? {
        val colorablePos = if(pos != null) ColorableSlidePos(
            pos,
            renderer.getFilledColor1(),
            renderer.getFilledColor2(),
            renderer.getOutlineColor1(),
            renderer.getOutlineColor2(),
            renderer.getWireColor1(),
            renderer.getWireColor2()
        ) else null

        if(colorablePos != null) {
            processedPossesList[colorablePos] = System.currentTimeMillis()
        }

        return colorablePos
    }

    open fun update(
        pos : BlockPos?,
        renderer : RenderingRewritePattern
    ) {
        val colorablePos = updatePosses(pos, renderer)

        if(colorablePos != lastBlockPos) {
            currentPos = if (colorablePos != null) i2d(colorablePos) else null
            prevPos = lastRenderPos ?: currentPos
            lastUpdateTime = System.currentTimeMillis()
            if (lastBlockPos == null) {
                startTime = System.currentTimeMillis()
            }

            lastBlockPos = colorablePos
        }
    }
}
