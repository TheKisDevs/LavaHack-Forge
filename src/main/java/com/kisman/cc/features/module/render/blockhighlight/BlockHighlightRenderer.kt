package com.kisman.cc.features.module.render.blockhighlight

import com.kisman.cc.features.module.combat.autorer.AutoRerUtil
import com.kisman.cc.features.module.combat.autorer.util.Easing
import com.kisman.cc.settings.util.RenderingRewritePattern
import com.kisman.cc.util.Colour
import com.kisman.cc.util.enums.BoxRenderModes
import com.kisman.cc.util.render.objects.Box
import com.kisman.cc.util.render.objects.BoxObject
import com.kisman.cc.util.render.objects.TextOnBlockObject
import net.minecraft.client.Minecraft
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
    var lastSelection: Selection? = null

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
        lastSelection = null
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
        selection : Selection?,
        color : Colour,
        mode : BoxRenderModes,
        width : Float,
        depth : Boolean,
        alpha : Boolean,
        ticks : Float,
        offset : Double
    ) {
        update(selection)

        prevPos?.let { prevPos ->
            currentPos?.let { currentPos ->
                val multiplier = Easing.OUT_QUART.inc(Easing.toDelta(lastUpdateTime, movingLength))
                val renderPos = prevPos.add(currentPos.subtract(prevPos).scale(multiplier.toDouble()))
                scale = if (selection != null) {
                    Easing.OUT_CUBIC.inc(Easing.toDelta(startTime, fadeLength))
                } else {
                    Easing.IN_CUBIC.dec(Easing.toDelta(startTime, fadeLength))
                }

                BoxObject(
                    Box.byAABB(toRenderBox(renderPos, scale, (selection ?: lastSelection!!)).grow(offset)),
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

    private inline fun toRenderBox(vec3d : Vec3d, scale : Float, selection : Selection): AxisAlignedBB {
        val halfSizeX = getSizeCoefficient(selection, 1) * scale
        val halfSizeY = getSizeCoefficient(selection, 2) * scale
        val halfSizeZ = getSizeCoefficient(selection, 3) * scale
        return AxisAlignedBB(
            vec3d.x - halfSizeX + getSizeCoefficient(selection, 1), vec3d.y - halfSizeY + getSizeCoefficient(selection, 2), vec3d.z - halfSizeZ + getSizeCoefficient(selection, 3),
            vec3d.x + halfSizeX + getSizeCoefficient(selection, 1), vec3d.y + halfSizeY + getSizeCoefficient(selection, 2), vec3d.z + halfSizeZ + getSizeCoefficient(selection, 3)
        )
    }

    private inline fun getSizeCoefficient(selection : Selection?, axis : Int) : Double {
        val bb = selection?.bb ?: return 0.5
        return when(axis) {
            1 -> (max(bb.maxX, bb.minX) - min(bb.minX, bb.maxX)) / 2 // X
            2 -> (max(bb.maxY, bb.minY) - min(bb.minY, bb.maxY)) / 2 // Y
            3 -> (max(bb.maxZ, bb.minZ) - min(bb.minZ, bb.maxZ)) / 2 // Z
            else -> 0.5
        }
    }

    private fun getVec(selection : Selection?) : Vec3d? {
        val bb = selection?.bb ?: return Vec3d(0.0, 0.0, 0.0)
        return Vec3d(min(bb.minX, bb.maxX), min(bb.minY, bb.maxY), min(bb.minZ, bb.maxZ))
//        return (if(selection?.pos != null) AutoRerUtil.toVec3dCenter(selection.pos) else Vec3d(min(bb?.minX!!, bb.maxX), min(bb.minY, bb.maxY), min(bb.minZ, bb.maxZ)))
    }

    fun update(selection: Selection?) {
        vec = getVec(selection)
        if (vec != lastVec) {
            currentPos = vec
            prevPos = lastRenderPos ?: currentPos
            lastUpdateTime = System.currentTimeMillis()
            if (lastSelection == null) startTime = System.currentTimeMillis()

            lastSelection = selection
            lastVec = vec
        }
    }
}