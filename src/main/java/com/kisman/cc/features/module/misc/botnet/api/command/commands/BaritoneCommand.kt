package com.kisman.cc.features.module.misc.botnet.api.command.commands

import com.kisman.cc.features.module.misc.botnet.api.command.BotCommand
import com.kisman.cc.features.module.misc.botnet.api.command.ExecutingType

class BaritoneCommand : BotCommand(
    arrayOf("baritone", "b"),
    ExecutingType.RAW) {

    override fun execute(command: String) {
        if(command.contains("b")) mc.player.sendChatMessage(command.replace("b ", "#"))
        else if (command.contains("baritone")) mc.player.sendChatMessage(command.replace("baritone ", "#"))
    }

}