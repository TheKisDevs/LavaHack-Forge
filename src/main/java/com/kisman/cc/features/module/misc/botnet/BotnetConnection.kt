package com.kisman.cc.features.module.misc.botnet

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.misc.botnet.api.command.CommandExecutor
import com.kisman.cc.features.module.misc.botnet.api.WebsiteConnection
import com.kisman.cc.features.module.misc.botnet.api.command.BotCommandManager
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.chat.other.ChatUtils
import java.text.SimpleDateFormat
import java.util.*



class BotnetConnection : Module(
    "BotnetConnection",
    "Connects you into a botnet using telegra.ph service",
    Category.MISC)
{
    var wc: WebsiteConnection? = null
    var last_cmd = ""




    override fun onEnable() {

        when(mode.valEnum) {
            Modes.Optimized -> {
                // SimpleDateFormat("MM/dd").format(Date())
                var i = 1
                while(true) {
                    wc = WebsiteConnection("https://telegra.ph/botnet-input-${SimpleDateFormat("MM/dd").format(Date())}-$i")
                    if(wc!!.checkConnection()) i++
                    else break
                }
            }

            Modes.Slow -> {
                wc = WebsiteConnection(input_url.valString)
                if (!wc!!.checkConnection()) isToggled = false
            }
        }


        BotCommandManager.init()
    }

    override fun onDisable() {
        ChatUtils.message("Left the botnet")
        wc = null
    }

    override fun update() {
        val cmd = wc!!.getInput()

        if(cmd != last_cmd) CommandExecutor.execute(cmd)
    }

    private enum class Modes {
        Optimized, Slow
    }

    var mode = register(Setting("Mode", this, Modes.Optimized))
    var input_url = register(Setting("Input URL", this, ""))
}