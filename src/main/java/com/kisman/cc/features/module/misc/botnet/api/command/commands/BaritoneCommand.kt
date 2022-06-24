package com.kisman.cc.features.module.misc.botnet.api.command.commands

import com.kisman.cc.features.module.misc.botnet.api.command.BotCommand
import com.kisman.cc.features.module.misc.botnet.api.command.ExecutingType

class BaritoneCommand : BotCommand(
    arrayOf("axis", "highway", "blacklist", "build", "farm", "goto", "mine", "sel", "selection", "s", "schematica"),
    ExecutingType.RAW) {

    override fun execute(command: String) {
        mc.player.sendChatMessage("#$command")
    }

}