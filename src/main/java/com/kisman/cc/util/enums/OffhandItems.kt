package com.kisman.cc.util.enums

import net.minecraft.init.Items
import net.minecraft.item.Item

/**
 * @author _kisman_
 * @since 16:02 of 22.08.2022
 */
enum class OffhandItems(
    val item : Item
) {
    Crystal(Items.END_CRYSTAL),
    Gap(Items.GOLDEN_APPLE),
    Pearl(Items.ENDER_PEARL),
    Chorus(Items.CHORUS_FRUIT),
    Shield(Items.SHIELD),
    Totem(Items.TOTEM_OF_UNDYING)
}