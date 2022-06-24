package com.kisman.cc.features.module.misc.botnet

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.misc.botnet.api.command.CommandExecutor
import com.kisman.cc.features.module.misc.botnet.api.WebsiteConnection
import com.kisman.cc.features.module.misc.botnet.api.command.BotCommandManager
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.chat.other.ChatUtils

class BotnetConnection : Module(
    "BotnetConnection",
    "Connects you into a botnet using telegra.ph service",
    Category.MISC)
{
    var wc: WebsiteConnection? = null
    var last_cmd = "";

    override fun onEnable() {
        wc = WebsiteConnection(input_url.valString)
        if (!wc!!.checkConnection()) isToggled = false

        BotCommandManager.init()
    }

    override fun onDisable() {
        ChatUtils.message("Left the botnet")
        wc = null;
    }

    override fun update() {
        val cmd = wc!!.getInput()

        if(cmd != last_cmd) CommandExecutor.execute(cmd)
    }

    var input_url = register(Setting("Input URL", this, ""))
}