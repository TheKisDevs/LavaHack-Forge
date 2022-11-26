package com.kisman.cc.util.enums

import net.minecraft.util.EnumFacing

/**
 * @author _kisman_
 * @since 17:30 of 26.11.2022
 */
enum class DiagonalDirections(
    val direction1 : EnumFacing,
    val direction2 : EnumFacing,
    val displayName : String
) {
    XpZp(EnumFacing.EAST, EnumFacing.SOUTH, "X+ Z+"),
    XmZm(EnumFacing.WEST, EnumFacing.NORTH, "X- Z-"),
    XpZm(EnumFacing.EAST, EnumFacing.NORTH, "X+ Z-"),
    XmZp(EnumFacing.WEST, EnumFacing.SOUTH, "X- Z+")

    ;

    override fun toString() : String = displayName
}