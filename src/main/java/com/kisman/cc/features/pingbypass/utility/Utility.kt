package com.kisman.cc.features.pingbypass.utility

import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.TimerUtils
import net.minecraft.util.text.TextComponentString

/**
 * @author _kisman_
 * @since 21:40 of 19.08.2022
 */

fun disconnect() {
    mc.player.connection.networkManager.closeChannel(TextComponentString("kill yourself"))
}

private val dotTimer = TimerUtils()
private val undTimer = TimerUtils()

private var dots = ""

fun getDots() : String {
    if (dotTimer.passedMillis(500)) {
        dots += "."
        dotTimer.reset()
    }
    if (dots.length > 3) {
        dots = ""
    }
    return dots
}

fun getUnderscore() : String {
    return if (!undTimer.passedMillis(500)) {
        "_"
    } else {
        if (undTimer.passedMillis(1000)) {
            undTimer.reset()
        }
        ""
    }
}