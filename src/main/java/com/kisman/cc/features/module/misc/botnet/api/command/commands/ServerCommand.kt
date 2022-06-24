package com.kisman.cc.features.module.misc.botnet.api.command.commands

import com.kisman.cc.features.module.misc.botnet.api.command.BotCommand
import com.kisman.cc.features.module.misc.botnet.api.command.ExecutingType

class ServerCommand: BotCommand("server", ExecutingType.ARGS) {

    override fun execute(args: Array<String?>) {
        when(args[0]) {

            "join" -> {

            }

            "leave" -> {


            }
        }
    }


}