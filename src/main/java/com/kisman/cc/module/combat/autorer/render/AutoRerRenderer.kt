package com.kisman.cc.module.combat.autorer.render

import com.kisman.cc.module.combat.autorer.PlaceInfo
import com.kisman.cc.module.combat.autorer.util.Easing
import com.kisman.cc.module.combat.autorer.util.ProjectionUtils
import com.kisman.cc.util.Colour
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

object AutoRerRenderer {
    @JvmField
    var lastBlockPos: BlockPos? = null

    @JvmField
    var prevPos: Vec3d? = null

    @JvmField
    var currentPos: Vec3d? = null

    @JvmField
    var lastRenderPos: Vec3d? = null

    @JvmField
    var lastUpdateTime = 0L

    @JvmField
    var startTime = 0L

    @JvmField
    var scale = 0.0f

    @JvmField
    var lastSelfDamage = 0.0f

    @JvmField
    var lastTargetDamage = 0.0f

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

    fun onRenderWorld(ticks: Float, filledAlpha: Float, outlineAlpha: Float, movingLength: Float, fadeLength: Float, color: Colour) {
        val filled = filledAlpha > 0
        val outline = outlineAlpha > 0
        val flag = filled || outline

        if (flag) {
//            val placeInfo = renderPlaceInfo
//            update(placeInfo)

            prevPos?.let { prevPos ->
                currentPos?.let { currentPos ->
                    val multiplier = Easing.OUT_QUART.inc(Easing.toDelta(lastUpdateTime, movingLength))
                    val renderPos = prevPos.add(currentPos.subtract(prevPos).scale(multiplier.toDouble()))
//                    scale = if (placeInfo != null) {
//                        Easing.OUT_CUBIC.inc(Easing.toDelta(startTime, fadeLength))
//                    } else {
//                        Easing.IN_CUBIC.dec(Easing.toDelta(startTime, fadeLength))
//                    }

                    val box = toRenderBox(renderPos, scale)

//                    renderer.aFilled = (filledAlpha * scale).toInt()
//                    renderer.aOutline = (outlineAlpha * scale).toInt()
//                    renderer.add(box, color)
//                    renderer.render(false)

                    lastRenderPos = renderPos
                }
            }
        }
    }

    fun onRender(ticks: Float) {
        if (scale != 0.0f) {
            lastRenderPos?.let {
                val text = buildString {
                    append("%.1f".format(lastTargetDamage))
                    if (this.isNotEmpty()) append('/')
                    append("%.1f".format(lastSelfDamage))
                }

                val screenPos = ProjectionUtils.toAbsoluteScreenPos(it)
                val alpha = (255.0f * scale).toInt()
                val color = if (scale == 1.0f) Colour(255, 255, 255) else Colour(255, 255, 255, alpha)

//                MainFontRenderer.drawString(
//                    text,
//                    screenPos.x.toFloat() - MainFontRenderer.getWidth(text, 2.0f) * 0.5f,
//                    screenPos.y.toFloat() - MainFontRenderer.getHeight(2.0f) * 0.5f,
//                    color,
//                    2.0f
//                )
            }
        }
    }

    inline fun toRenderBox(vec3d: Vec3d, scale: Float): AxisAlignedBB {
        val halfSize = 0.5 * scale
        return AxisAlignedBB(
            vec3d.x - halfSize, vec3d.y - halfSize, vec3d.z - halfSize,
            vec3d.x + halfSize, vec3d.y + halfSize, vec3d.z + halfSize
        )
    }

    fun update(placeInfo: PlaceInfo) {
        val newBlockPos = placeInfo?.blockPos
        if (newBlockPos != lastBlockPos) {
            if (placeInfo != null) {
//                currentPos = placeInfo.blockPos.toVec3dCenter()
                prevPos = lastRenderPos ?: currentPos
                lastUpdateTime = System.currentTimeMillis()
                if (lastBlockPos == null) startTime = System.currentTimeMillis()
            } else {
                lastUpdateTime = System.currentTimeMillis()
                if (lastBlockPos != null) startTime = System.currentTimeMillis()
            }

            lastBlockPos = newBlockPos
        }

        if (placeInfo != null) {
            lastSelfDamage = placeInfo.selfDamage
            lastTargetDamage = placeInfo.targetDamage
        }
    }
}