package com.kisman.cc.util.render

import net.minecraft.util.EnumFacing

/**
 * @author _kisman_
 * @since 17:35 of 30.03.2023
 */
fun EnumFacing.left() : EnumFacing = if(this == EnumFacing.EAST) {
    EnumFacing.NORTH
} else if(this == EnumFacing.NORTH) {
    EnumFacing.EAST
} else if(this == EnumFacing.EAST) {
    EnumFacing.SOUTH
} else if(this == EnumFacing.SOUTH) {
    EnumFacing.EAST
} else {
    null!!
}

fun EnumFacing.right() : EnumFacing = left().opposite