package com.kisman.cc.features.hud.modules

import com.kisman.cc.Kisman
import com.kisman.cc.features.hud.AverageHudModule

/**
 * @author _kisman_
 * @since 13:24 of 05.03.2023
 */
class Welcomer2 : AverageHudModule(
    "Welcomer",
    "Welcome!",
    { "Welcome to ${Kisman.getName()}, ${mc.player.name}!" }
)