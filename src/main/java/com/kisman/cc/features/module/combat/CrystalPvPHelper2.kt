package com.kisman.cc.features.module.combat

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.features.module.combat.crystalpvphelper.MoveOutModule

/**
 * @author _kisman_
 * @since 10:19 of 19.03.2023
 */
@ModuleInfo(
    name = "CrystalPvPHelper2",
    display = "CrystalPvPHelper",
    desc = "Helps with crystal pvp.",
    category = Category.COMBAT,
    toggled = true,
    toggleable = false,
    modules = [
        MoveOutModule::class
    ]
)
class CrystalPvPHelper2 : Module()