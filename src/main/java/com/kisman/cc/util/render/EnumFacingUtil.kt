package com.kisman.cc.util.render

import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import kotlin.math.abs

/**
 * @author _kisman_
 * @since 18:11 of 18.10.2022
 */

val x_plus = EnumFacing.EAST
val x_minus = EnumFacing.WEST

val z_plus = EnumFacing.SOUTH
val z_minus = EnumFacing.NORTH

fun nearestFacing(
    pos1 : BlockPos,
    pos2 : BlockPos
) : EnumFacing {
    val xDifferent = pos1.x - pos2.x
    val zDifferent = pos1.z - pos2.z

    var nearestFacing : EnumFacing

    nearestFacing = if(xDifferent < 0) {
        x_plus
    } else if(xDifferent > 0) {
        x_minus
    } else {
        if(zDifferent < 0) {
            z_minus
        } else if(zDifferent > 0) {
            z_plus
        } else {
            EnumFacing.EAST
        }
    }

    if(abs(zDifferent) > abs(xDifferent)) {
        if (zDifferent < 0) {
            nearestFacing = z_minus
        } else if (zDifferent > 0) {
            nearestFacing = z_plus
        }
    }

    return nearestFacing
}

fun EnumFacing.left() : EnumFacing = if(this == x_plus) {
    z_minus
} else if(this == z_minus) {
    x_minus
} else if(this == x_minus) {
    z_plus
} else if(this == z_plus) {
    x_plus
} else {
    null!!
}

fun EnumFacing.right() : EnumFacing = left().opposite