package com.kisman.cc.features.hud.modules

import com.kisman.cc.features.hud.AverageHudModule
import com.kisman.cc.util.manager.Managers
import net.minecraft.util.text.TextFormatting

/**
 * @author _kisman_
 * @since 13:34 of 05.03.2023
 */
class CrystalPerSecond2 : AverageHudModule(
    "CrystalPerSecond",
    "Shows current crystals/sec value",
    { "Crystals/Sec: ${TextFormatting.GRAY}${Managers.instance.cpsManager.getCPS()}" }
) {
    init {
        displayName = "Crystals/Sec"
    }
}