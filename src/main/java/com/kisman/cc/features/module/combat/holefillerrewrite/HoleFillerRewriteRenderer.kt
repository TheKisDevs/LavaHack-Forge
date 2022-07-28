package com.kisman.cc.features.module.combat.holefillerrewrite

import com.kisman.cc.features.module.combat.autorer.AutoRerUtil
import com.kisman.cc.features.module.combat.autorer.util.Easing
import com.kisman.cc.settings.util.RenderingRewritePattern
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

/**
 * @author _kisman_
 * @since 19:03 of 07.07.2022
 */
class HoleFillerRewriteRenderer {
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

    fun reset() {
        lastBlockPos = null
        prevPos = null
        currentPos = null
        lastRenderPos = null
        lastUpdateTime = 0L
        startTime = 0L
        scale = 0.0f
    }

    fun onRenderWorld(
        movingLength: Float,
        fadeLength: Float,
        renderer : RenderingRewritePattern,
        placeInfo : PlaceInfo,
        needTarget : Boolean
    ) {
        update(placeInfo)

        prevPos?.let { prevPos ->
            currentPos?.let { currentPos ->
                val multiplier = Easing.OUT_QUART.inc(Easing.toDelta(lastUpdateTime, movingLength))
                val renderPos = prevPos.add(currentPos.subtract(prevPos).scale(multiplier.toDouble()))
                scale = if (placeInfo.blockPos != null && (!needTarget || placeInfo.target != null)) {
                    Easing.OUT_CUBIC.inc(Easing.toDelta(startTime, fadeLength))
                } else {
                    Easing.IN_CUBIC.dec(Easing.toDelta(startTime, fadeLength))
                }

                renderer.draw(toRenderBox(renderPos, scale))

                lastRenderPos = renderPos
            }
        }
    }

    private inline fun toRenderBox(vec3d: Vec3d, scale: Float): AxisAlignedBB {
        val halfSize = 0.5 * scale
        return AxisAlignedBB(
            vec3d.x - halfSize + 0.5, vec3d.y - halfSize + 0.5, vec3d.z - halfSize + 0.5,
            vec3d.x + halfSize + 0.5, vec3d.y + halfSize + 0.5, vec3d.z + halfSize + 0.5
        )
    }

    fun update(placeInfo: PlaceInfo) {
        val newBlockPos = placeInfo.blockPos
        if (newBlockPos != lastBlockPos) {
            currentPos = if(placeInfo.blockPos != null) AutoRerUtil.toVec3dCenter(placeInfo.blockPos!!) else null
            prevPos = lastRenderPos ?: currentPos
            lastUpdateTime = System.currentTimeMillis()
            if (lastBlockPos == null) startTime = System.currentTimeMillis()

            lastBlockPos = newBlockPos
        }
    }
}