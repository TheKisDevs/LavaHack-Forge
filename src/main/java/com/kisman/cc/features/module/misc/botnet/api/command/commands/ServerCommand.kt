package com.kisman.cc.features.module.misc.botnet.api.command.commands

import com.kisman.cc.features.module.misc.botnet.api.command.BotCommand

class ServerCommand: BotCommand("server", true) {

    override fun perform(args: Array<String?>) {
        when(args[0]) {

            "join" -> {

            }

            "leave" -> {


            }
        }
    }


}