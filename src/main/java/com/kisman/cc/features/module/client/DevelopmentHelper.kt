package com.kisman.cc.features.module.client

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.enums.DevelopmentHelperSlotTypes

/**
 * @author _kisman_
 * @since 21:29 of 11.11.2022
 */
class DevelopmentHelper : Module(
    "DevelopmentHelper",
    "Helps with lavahack development.",
    Category.CLIENT
) {
    val displaySlots = register(Setting("Display Slots", this, false))
    val slotType = register(Setting("Slot Type", this, DevelopmentHelperSlotTypes.Index))

    init {
        instance = this
    }

    companion object {
        @JvmStatic
        var instance : DevelopmentHelper? = null
    }
}