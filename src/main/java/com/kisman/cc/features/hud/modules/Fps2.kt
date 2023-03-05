package com.kisman.cc.features.hud.modules

import com.kisman.cc.features.hud.AverageHudModule
import net.minecraft.client.Minecraft
import net.minecraft.util.text.TextFormatting

/**
 * @author _kisman_
 * @since 13:11 of 05.03.2023
 */
class Fps2 : AverageHudModule(
    "Fps",
    "Shows current fps",
    { "Fps: ${TextFormatting.GRAY}${Minecraft.getDebugFPS()}" }
)