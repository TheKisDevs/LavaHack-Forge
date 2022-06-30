package com.kisman.cc.features.module.render.blockhighlight

import com.kisman.cc.features.module.combat.autorer.util.Easing
import com.kisman.cc.features.module.combat.autorer.util.mask.EnumFacingMask
import com.kisman.cc.util.Colour
import com.kisman.cc.util.enums.BoxRenderModes
import com.kisman.cc.util.render.objects.Box
import com.kisman.cc.util.render.objects.BoxObject
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import kotlin.math.max
import kotlin.math.min

/**
 * @author _kisman_
 * @since 10:18 of 07.06.2022
 */
class BlockHighlightRenderer {
    @JvmField
    var vec : Vec3d? = null

    @JvmField
    var lastVec : Vec3d? = null

    @JvmField
    var lastBB: AxisAlignedBB? = null

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
        lastBB = null
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
        bb : AxisAlignedBB?,
        color : Colour,
        mode : BoxRenderModes,
        width : Float,
        depth : Boolean,
        alpha : Boolean,
        ticks : Float,
        offset : Double,
        facing : EnumFacing?
    ) {
        update(bb)

        prevPos?.let { prevPos ->
            currentPos?.let { currentPos ->
                val multiplier = Easing.OUT_QUART.inc(Easing.toDelta(lastUpdateTime, movingLength))
                val renderPos = prevPos.add(currentPos.subtract(prevPos).scale(multiplier.toDouble()))
                scale = if (bb != null) {
                    Easing.OUT_CUBIC.inc(Easing.toDelta(startTime, fadeLength))
                } else {
                    Easing.IN_CUBIC.dec(Easing.toDelta(startTime, fadeLength))
                }

                var bb = toRenderBox(renderPos, scale, (bb ?: lastBB!!)).grow(offset)

                if(facing != null) {
                    bb = EnumFacingMask.toAABB(bb, facing)
                }

                BoxObject(
                    Box.byAABB(bb),
                    color,
                    mode,
                    width,
                    depth,
                    alpha
                ).draw(ticks)

                lastRenderPos = renderPos
            }
        }
    }

    private inline fun toRenderBox(vec3d : Vec3d, scale : Float, bb : AxisAlignedBB): AxisAlignedBB {
        val halfSizeX = getSizeCoefficient(bb, 1) * scale
        val halfSizeY = getSizeCoefficient(bb, 2) * scale
        val halfSizeZ = getSizeCoefficient(bb, 3) * scale
        return AxisAlignedBB(
            vec3d.x - halfSizeX + getSizeCoefficient(bb, 1), vec3d.y - halfSizeY + getSizeCoefficient(bb, 2), vec3d.z - halfSizeZ + getSizeCoefficient(bb, 3),
            vec3d.x + halfSizeX + getSizeCoefficient(bb, 1), vec3d.y + halfSizeY + getSizeCoefficient(bb, 2), vec3d.z + halfSizeZ + getSizeCoefficient(bb, 3)
        )
    }

    private inline fun getSizeCoefficient(bb : AxisAlignedBB?, axis : Int) : Double {
        return when(axis) {
            1 -> (max(bb?.maxX!!, bb.minX) - min(bb.minX, bb.maxX)) / 2 // X
            2 -> (max(bb?.maxY!!, bb.minY) - min(bb.minY, bb.maxY)) / 2 // Y
            3 -> (max(bb?.maxZ!!, bb.minZ) - min(bb.minZ, bb.maxZ)) / 2 // Z
            else -> 0.5
        }
    }

    private fun getVec(bb : AxisAlignedBB?) : Vec3d? {
        val bb = bb ?: return Vec3d(0.0, 0.0, 0.0)
        return Vec3d(min(bb.minX, bb.maxX), min(bb.minY, bb.maxY), min(bb.minZ, bb.maxZ))
//        return (if(selection?.pos != null) AutoRerUtil.toVec3dCenter(selection.pos) else Vec3d(min(bb?.minX!!, bb.maxX), min(bb.minY, bb.maxY), min(bb.minZ, bb.maxZ)))
    }

    fun update(bb : AxisAlignedBB?) {
        vec = getVec(bb)
        if (vec != lastVec) {
            currentPos = vec
            prevPos = lastRenderPos ?: currentPos
            lastUpdateTime = System.currentTimeMillis()
            if (lastBB == null) startTime = System.currentTimeMillis()

            lastBB = bb
            lastVec = vec
        }
    }
}