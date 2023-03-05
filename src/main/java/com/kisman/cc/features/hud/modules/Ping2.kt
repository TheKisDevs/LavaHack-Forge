package com.kisman.cc.features.hud.modules

import com.kisman.cc.features.hud.AverageHudModule
import com.kisman.cc.util.getPing
import net.minecraft.util.text.TextFormatting

/**
 * @author _kisman_
 * @since 13:13 of 05.03.2023
 */
class Ping2 : AverageHudModule(
    "Ping",
    "Shows your current ping",
    { "Ping: ${TextFormatting.GRAY}${getPing()}" }
)