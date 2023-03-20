package com.kisman.cc.features.module.movement

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.features.module.movement.fly.BoatFly
import com.kisman.cc.features.module.movement.fly.ElytraFly
import com.kisman.cc.features.module.movement.fly.NormalFly
import com.kisman.cc.features.module.movement.fly.PacketFly

/**
 * @author _kisman_
 * @since 10:54 of 19.03.2023
 */
@ModuleInfo(
    name = "Flight",
    display = "Flight",
    category = Category.MOVEMENT,
    modules = [
        BoatFly::class,
        ElytraFly::class,
        NormalFly::class,
        PacketFly::class
    ]
)
class Flight : Module()