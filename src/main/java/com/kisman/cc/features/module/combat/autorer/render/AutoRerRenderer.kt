package com.kisman.cc.features.module.combat.autorer.render

import com.kisman.cc.features.module.combat.autorer.AutoRerUtil
import com.kisman.cc.features.module.combat.autorer.PlaceInfo
import com.kisman.cc.features.module.combat.autorer.util.Easing
import com.kisman.cc.settings.util.RenderingRewritePattern
import com.kisman.cc.util.Colour
import com.kisman.cc.util.render.objects.world.TextOnBlockObject
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

class AutoRerRenderer {
    @JvmField var lastBlockPos : BlockPos? = null
    @JvmField var prevPos : Vec3d? = null
    @JvmField var currentPos : Vec3d? = null
    @JvmField var lastRenderPos : Vec3d? = null
    @JvmField var lastUpdateTime = 0L
    @JvmField var startTime = 0L
    @JvmField var scale = 0.0f
    @JvmField var lastSelfDamage = 0.0f
    @JvmField var lastTargetDamage = 0.0f
    @JvmField var timePassed = 0f
    @JvmField var lastBB : AxisAlignedBB? = null

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
        timePassed = 0f
        lastBB = null
    }

    fun onRenderWorld(
        movingLength : Float,
        fadeLength : Float,
        renderer : RenderingRewritePattern,
        placeInfo : PlaceInfo,
        text : Boolean,
        test : Boolean
    ) {
        update(placeInfo)

        prevPos?.let { prevPos ->
            (currentPos ?: prevPos).let { currentPos ->
                scale = if (this.currentPos != null) {
                    Easing.OUT_CUBIC.inc(Easing.toDelta(startTime, fadeLength))
                } else {
                    Easing.IN_CUBIC.dec(Easing.toDelta(startTime, fadeLength))
                }

                timePassed = if (lastBB == toRenderBox(currentPos, scale)) {
                    0f
                } else {
                    50.0f
                }

                val multiplier = if(test) {
                    timePassed / 900f * 0.8f
                } else {
                    Easing.OUT_QUART.inc(Easing.toDelta(lastUpdateTime, movingLength))
                }

                val renderPos = prevPos.add(currentPos.subtract(prevPos).scale(multiplier.toDouble()))

                renderer.draw(toRenderBox(renderPos, scale).also { lastBB = it })

                lastRenderPos = renderPos

                if(text) {
                    val text_ = buildString {
                        append("%.1f".format(lastTargetDamage))
                        if (this.isNotEmpty()) append('/')
                        append("%.1f".format(lastSelfDamage))
                    }

                    TextOnBlockObject(
                        text_,
                        BlockPos(currentPos),
                        Colour(255, 255, 255, (255.0f * scale).toInt())
                    )
                }
            }
        }
    }

    private fun toRenderBox(vec3d: Vec3d, scale: Float): AxisAlignedBB {
        val halfSize = 0.5 * scale
        return AxisAlignedBB(
            vec3d.x - halfSize + 0.5, vec3d.y - halfSize + 0.5, vec3d.z - halfSize + 0.5,
            vec3d.x + halfSize + 0.5, vec3d.y + halfSize + 0.5, vec3d.z + halfSize + 0.5
        )
    }

    fun update(placeInfo: PlaceInfo) {
        val newBlockPos = placeInfo.blockPos
        if (newBlockPos != lastBlockPos) {
            currentPos = if(newBlockPos != null) AutoRerUtil.toVec3dCenter(newBlockPos) else null
            prevPos = lastRenderPos ?: currentPos
            lastUpdateTime = System.currentTimeMillis()
            if (lastBlockPos == null) startTime = System.currentTimeMillis()

            lastBlockPos = newBlockPos
        }
        if(!placeInfo.selfDamage.isNaN() && !placeInfo.targetDamage.isNaN()) {
            lastSelfDamage = placeInfo.selfDamage
            lastTargetDamage = placeInfo.targetDamage
        } else {
            lastSelfDamage = 0f
            lastTargetDamage = 0f
        }
    }
}