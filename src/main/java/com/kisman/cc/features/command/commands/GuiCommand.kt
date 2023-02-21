package com.kisman.cc.features.command.commands

import com.kisman.cc.features.command.Command
import com.kisman.cc.features.module.client.GuiModule


/**
 * @author _kisman_
 * @since 8:42 of 19.02.2023
 */
class GuiCommand : Command(
    "gui"
) {
    override fun runCommand(
        s : String,
        args : Array<String>
    ) {
        GuiModule.instance.isToggled = true
    }

    override fun getDescription() : String = "Opens gui"

    override fun getSyntax() : String = "gui"

}