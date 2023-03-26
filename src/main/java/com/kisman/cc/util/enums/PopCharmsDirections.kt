package com.kisman.cc.util.enums

import net.minecraft.util.EnumFacing

/**
 * @author _kisman_
 * @since 8:53 of 24.03.2023
 */
enum class PopCharmsDirections(
    private val facing : EnumFacing?
) {
    Up(EnumFacing.UP),
    Down(EnumFacing.DOWN),
    Random(null) {
        override fun facing() = if(java.util.Random().nextInt() % 2 == 0) {
            EnumFacing.UP
        } else {
            EnumFacing.DOWN
        }
    }

    ;

    open fun facing() : EnumFacing = facing!!
}