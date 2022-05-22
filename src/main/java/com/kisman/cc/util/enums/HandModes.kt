package com.kisman.cc.util.enums

import net.minecraft.util.EnumHand

/**
 * @author _kisman_
 * @since 10:23 of 21.05.2022
 */
enum class HandModes(
    val hand : EnumHand
) {
    MainHand(EnumHand.MAIN_HAND),
    OffHand(EnumHand.OFF_HAND)
}