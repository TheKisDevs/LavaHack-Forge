package com.kisman.cc.pingbypass.utils

import com.kisman.cc.util.Globals.mc
import net.minecraft.util.text.TextComponentString

/**
 * @author _kisman_
 * @since 0:43 of 21.08.2022
 */

fun disconnectFromMC(
    reason : String
) {
    if(mc.connection != null) {
        mc.connection!!.networkManager.closeChannel(TextComponentString(reason))
    }
}