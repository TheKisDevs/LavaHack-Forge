package com.kisman.cc.util.render.pattern

import com.kisman.cc.settings.util.RenderingRewritePattern
import com.kisman.cc.settings.util.SlideRenderingRewritePattern
import com.kisman.cc.util.Colour
import com.kisman.cc.util.Globals.mc
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
@Suppress("BooleanLiteralArgument")
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

    open fun handleRenderWorldStatic(
        renderer : RenderingRewritePattern,
        pos : BlockPos?,
        text : String?
    ) {
        handleRenderWorldStatic(
            renderer,
            { it },
            pos,
            text
        )
    }

    open fun handleRenderWorldStatic(
        renderer : RenderingRewritePattern,
        aabbModifier : (AxisAlignedBB) -> AxisAlignedBB,
        pos : BlockPos?,
        text : String?
    ) {
        handleRenderWorld(
            0f,
            0f,
            0f,
            renderer,
            aabbModifier,
            pos,
            text
        )
    }

    open fun handleRenderWorld(
        movingLength : Float,
        fadeLength : Float,
        mutationLength : Float,
        alphaFade : Boolean,
        aabbMutation : Boolean,
        renderer : RenderingRewritePattern,
        pos : BlockPos?,
        text : String?
    ) {
        update(pos, renderer)
        renderWorld(
            movingLength,
            fadeLength,
            mutationLength,
            alphaFade,
            aabbMutation,
            renderer,
            text
        )
    }

    open fun handleRenderWorld(
        movingLength : Float,
        fadeLength : Float,
        mutationLength : Float,
        renderer : RenderingRewritePattern,
        aabbModifier : (AxisAlignedBB) -> AxisAlignedBB,
        pos : BlockPos?,
        text : String?
    ) {
        update(pos, renderer)
        renderWorld(
            movingLength,
            fadeLength,
            mutationLength,
            false,
            false,
            renderer,
            aabbModifier,
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
            renderer.mutationLength.valFloat,
            renderer.alphaFadeMutation.valBoolean,
            renderer.aabbMutation.valBoolean,
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
            renderer.mutationLength.valFloat,
            renderer.alphaFadeMutation.valBoolean,
            renderer.aabbMutation.valBoolean,
            renderer,
            pos,
            text
        )
    }

    open fun renderWorld(
        movingLength : Float,
        fadeLength : Float,
        mutationLength : Float,
        alphaFade : Boolean,
        aabbMutation : Boolean,
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

        if(mutationLength != 0f && (alphaFade || aabbMutation)) {
            val keysToRemove = ArrayList<BlockPos>()

            for(entry in processedPossesList) {
                val pos = entry.key
                val time = entry.value
                val bb = mc.world.getBlockState(pos).getSelectedBoundingBox(mc.world, pos)

                if(multiplier == 0.0 && pos == currentPos) {
                    keysToRemove.add(pos)
                    continue
                }

                val alphaCoeff = mutation(
                    mutationLength,
                    renderer,
                    time
                )

                if(alphaCoeff == 0.0) {
                    keysToRemove.add(pos)
                    continue
                }

                renderer.draw(
                    if(aabbMutation) mutate(mutationLength, renderer, time, bb) else bb,
                    if(pos is ColorableSlidePos && alphaFade) pos.colour1 else renderer.colors.filledColor1.color(),
                    if(pos is ColorableSlidePos && alphaFade) pos.colour2 else renderer.colors.filledColor2.color(),
                    if(pos is ColorableSlidePos && alphaFade) pos.colour3 else renderer.colors.filledColor3.color(),
                    if(pos is ColorableSlidePos && alphaFade) pos.colour4 else renderer.colors.filledColor4.color(),
                    if(pos is ColorableSlidePos && alphaFade) pos.colour5 else renderer.colors.filledColor5.color(),
                    if(pos is ColorableSlidePos && alphaFade) pos.colour6 else renderer.colors.filledColor6.color(),
                    if(pos is ColorableSlidePos && alphaFade) pos.colour7 else renderer.colors.filledColor7.color(),
                    if(pos is ColorableSlidePos && alphaFade) pos.colour8 else renderer.colors.filledColor8.color(),
                    if(pos is ColorableSlidePos && alphaFade) pos.colour9 else renderer.colors.outlineColor1.color(),
                    if(pos is ColorableSlidePos && alphaFade) pos.colour10 else renderer.colors.outlineColor2.color(),
                    if(pos is ColorableSlidePos && alphaFade) pos.colour11 else renderer.colors.outlineColor3.color(),
                    if(pos is ColorableSlidePos && alphaFade) pos.colour12 else renderer.colors.outlineColor4.color(),
                    if(pos is ColorableSlidePos && alphaFade) pos.colour13 else renderer.colors.outlineColor5.color(),
                    if(pos is ColorableSlidePos && alphaFade) pos.colour14 else renderer.colors.outlineColor6.color(),
                    if(pos is ColorableSlidePos && alphaFade) pos.colour15 else renderer.colors.outlineColor7.color(),
                    if(pos is ColorableSlidePos && alphaFade) pos.colour16 else renderer.colors.outlineColor8.color(),
                    if(alphaFade) alphaCoeff else 1.0
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
        mutationLength : Float,
        alphaFade : Boolean,
        aabbMutation : Boolean,
        renderer : RenderingRewritePattern,
        text : String?
    ) {
        renderWorld(
            movingLength,
            fadeLength,
            mutationLength,
            alphaFade,
            aabbMutation,
            renderer,
            { it },
            text
        )
    }

    protected fun mutation(
        mutationLength : Float,
        renderer : RenderingRewritePattern,
        time : Long
    ) : Double = if(mutationLength != 0f) {
        if(renderer is SlideRenderingRewritePattern) {
            renderer.mutationEasing.valElement.inc(toDelta(time, mutationLength))
        } else {
            EasingEnum.Easing.OutQuart.dec(toDelta(time, mutationLength))
        }
    } else {
        1.0
    }

    protected fun mutate(
        mutationLength : Float,
        renderer : RenderingRewritePattern,
        time : Long,
        aabb : AxisAlignedBB
    ) = if(mutationLength != 0f && renderer is SlideRenderingRewritePattern) {
        renderer.aabbMutationLogic.valEnum.modifier.modify(aabb, mutation(mutationLength, renderer, time))
    } else {
        aabb
    }

    protected fun multiplier(
        movingLength : Float,
        renderer : RenderingRewritePattern
    ) : Double = if(movingLength != 0f) {
        if(renderer is SlideRenderingRewritePattern) {
            renderer.movingOutEasing.valElement.inc(toDelta(lastUpdateTime, movingLength))
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
                renderer.fadeOutEasing.valElement.inc(toDelta(startTime, fadeLength))
            } else {
                renderer.fadeInEasing.valElement.dec(toDelta(startTime, fadeLength))
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
            renderer.colors.filledColor1.color(),
            renderer.colors.filledColor2.color(),
            renderer.colors.filledColor3.color(),
            renderer.colors.filledColor4.color(),
            renderer.colors.filledColor5.color(),
            renderer.colors.filledColor6.color(),
            renderer.colors.filledColor7.color(),
            renderer.colors.filledColor8.color(),
            renderer.colors.outlineColor1.color(),
            renderer.colors.outlineColor2.color(),
            renderer.colors.outlineColor3.color(),
            renderer.colors.outlineColor4.color(),
            renderer.colors.outlineColor5.color(),
            renderer.colors.outlineColor6.color(),
            renderer.colors.outlineColor7.color(),
            renderer.colors.outlineColor8.color()
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
