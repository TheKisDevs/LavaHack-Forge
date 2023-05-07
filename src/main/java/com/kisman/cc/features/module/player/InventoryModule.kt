package com.kisman.cc.features.module.player

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.features.module.player.inventory.AutoArmor
import com.kisman.cc.features.module.player.inventory.OffHand
import com.kisman.cc.features.module.player.inventory.Refill
import com.kisman.cc.features.module.player.inventory.Replenish

/**
 * @author _kisman_
 * @since 11:18 of 05.05.2023
 */
@ModuleInfo(
    name = "Inventory",
    category = Category.PLAYER,
    toggled = true,
    toggleable = false,
    pingbypass = true,
    modules = [
        AutoArmor::class,
        OffHand::class,
        Refill::class,
        Replenish::class
    ]
)
class InventoryModule : Module()