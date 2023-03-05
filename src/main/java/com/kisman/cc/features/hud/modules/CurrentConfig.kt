package com.kisman.cc.features.hud.modules

import com.kisman.cc.Kisman
import com.kisman.cc.features.hud.AverageHudModule
import com.kisman.cc.features.hud.HudModule
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.render.customfont.CustomFontUtil
import com.kisman.cc.util.render.ColorUtils
import com.viaversion.viaversion.libs.kyori.adventure.text.format.TextFormat
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * @author _kisman_
 * @since 16:39 of 02.06.2022
 */
class CurrentConfig : AverageHudModule(
    "CurrentConfig",
    "Shows your current config.",
    { "Current config: ${TextFormatting.GRAY}${Kisman.currentConfig}" }
)