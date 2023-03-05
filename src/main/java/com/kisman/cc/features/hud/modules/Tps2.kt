package com.kisman.cc.features.hud.modules

import com.kisman.cc.features.hud.AverageHudModule
import com.kisman.cc.features.subsystem.subsystems.tps
import net.minecraft.util.text.TextFormatting

/**
 * @author _kisman_
 * @since 13:29 of 05.03.2023
 */
class Tps2 : AverageHudModule(
    "Tps",
    "Shows current tps of current server",
    { "Tps: ${TextFormatting.GRAY}$tps" }
)