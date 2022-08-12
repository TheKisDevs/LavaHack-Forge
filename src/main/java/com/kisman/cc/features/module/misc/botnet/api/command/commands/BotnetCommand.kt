package com.kisman.cc.features.module.misc.botnet.api.command.commands

import com.kisman.cc.Kisman
import com.kisman.cc.features.module.misc.botnet.BotnetConnection
import com.kisman.cc.features.module.misc.botnet.api.WebsiteConnection
import com.kisman.cc.features.module.misc.botnet.api.command.BotCommand
import com.kisman.cc.features.module.misc.botnet.api.command.ExecutingType
import com.kisman.cc.util.chat.cubic.ChatUtility

class BotnetCommand: BotCommand(
    "botnet", ExecutingType.ARGS
) {

    override fun execute(args: Array<String?>) {
        when(args[0]) {

            "stop" -> {
                Kisman.instance.moduleManager.getModule("BotnetConnection").toggled = false
                ChatUtility.message().printClientModuleMessage("Left the botnet")
            }

            "changeURL" ->
                if(args[1] != null && WebsiteConnection(args[1]!!).checkConnection())
                    //(ModuleManagerAPI.getModule("BotnetConnection") as BotnetConnection).input_url.valString = args[1]!!
                    (Kisman.instance.moduleManager.getModule("BotnetConnection") as BotnetConnection).input_url.valString = args[1]!!
        }
    }
}