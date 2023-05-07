package com.kisman.cc.features.module.misc

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.features.module.misc.announcer.*

/**
 * @author _kisman_
 * @since 10:05 of 24.04.2023
 */
@ModuleInfo(
    name = "Announcer",
    category = Category.MISC,
    toggled = true,
    toggleable = false,
    modules = [
        BurrowCounter::class,
        LogNotify::class,
        TotemPopCounterRewrite::class,
        TraceTeleport::class,
        VisualRange::class
    ]
)
class Announcer : Module()