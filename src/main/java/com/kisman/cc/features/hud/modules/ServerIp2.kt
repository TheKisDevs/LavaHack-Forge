package com.kisman.cc.features.hud.modules

import com.kisman.cc.features.hud.AverageHudModule
import net.minecraft.util.text.TextFormatting

/**
 * @author _kisman_
 * @since 13:32 of 05.03.2023
 */
class ServerIp2 : AverageHudModule(
    "ServerIp",
    "Shows ip of current server",
    { "ServerIp: ${TextFormatting.GRAY}${if(mc.isSingleplayer) "singleplayer" else mc.currentServerData.serverIP}" }
)