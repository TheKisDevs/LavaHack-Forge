package com.kisman.cc.features.module.misc.botnet.api.command

import com.kisman.cc.features.module.misc.botnet.api.command.commands.BaritoneCommand
import com.kisman.cc.features.module.misc.botnet.api.command.commands.BotnetCommand
import com.kisman.cc.features.module.misc.botnet.api.command.commands.ServerCommand


object BotCommandManager {
    var commands = arrayListOf<BotCommand>()

    fun init() {
        commands.clear()

        commands.add(ServerCommand())
        commands.add(BaritoneCommand())
        commands.add(BotnetCommand())
    }

}