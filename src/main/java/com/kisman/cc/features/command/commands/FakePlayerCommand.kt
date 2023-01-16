package com.kisman.cc.features.command.commands

import com.kisman.cc.Kisman
import com.kisman.cc.features.command.Command

/**
 * @author _kisman_
 * @since 16:58 of 16.01.2023
 */
class FakePlayerCommand : Command(
    "fakeplayer"
) {
    override fun runCommand(
        s : String,
        args : Array<String>
    ) {
        Kisman.instance.commandManager.runCommands("-toggle FakePlayer")
    }

    override fun getDescription() : String = "Uses FakePlayer module"

    override fun getSyntax() : String = "fakeplayer"
}