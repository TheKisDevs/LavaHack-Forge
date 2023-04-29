@file:Suppress("unused")

package com.kisman.cc.util.enums

import com.kisman.cc.util.render.Rendering
import net.minecraft.util.math.AxisAlignedBB

/**
 * @author _kisman_
 * @since 17:02 of 20.03.2023
 */
enum class AABBProgressModifiers(
    val modifier : IModifier
) {
    CentredBox(object : IModifier {
        override fun modify(
            aabb : AxisAlignedBB,
            percent : Double
        ) : AxisAlignedBB {
            fun offset(
                aabb : AxisAlignedBB
            ) : AxisAlignedBB = AxisAlignedBB(
                aabb.minX + 0.5,
                aabb.minY + 0.5,
                aabb.minZ + 0.5,
                aabb.maxX - 0.5,
                aabb.maxY - 0.5,
                aabb.maxZ - 0.5
            )

            return offset(Rendering.scale(aabb, percent))
        }
    }),

    BottomColumn(object : IModifier {
        override fun modify(
            aabb : AxisAlignedBB,
            percent : Double
        ) : AxisAlignedBB = AxisAlignedBB(
            aabb.minX,
            aabb.minY,
            aabb.minZ,
            aabb.maxX,
            aabb.maxY - (aabb.maxY - aabb.minY) * (1 - percent),
            aabb.maxZ
        )
    }),

    TopColumn(object : IModifier {
        override fun modify(
            aabb : AxisAlignedBB,
            percent : Double
        ) : AxisAlignedBB = AxisAlignedBB(
            aabb.minX,
            aabb.minY + (aabb.maxY - aabb.minY) * (1 - percent),
            aabb.minZ,
            aabb.maxX,
            aabb.maxY,
            aabb.maxZ
        )
    })
}

interface IModifier {
    fun modify(
        aabb : AxisAlignedBB,
        percent : Double
    ) : AxisAlignedBB
}