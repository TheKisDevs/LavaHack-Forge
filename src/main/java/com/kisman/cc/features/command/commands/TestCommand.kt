package com.kisman.cc.features.command.commands

import com.kisman.cc.features.command.Command
import com.kisman.cc.features.command.SubCommand

/**
 * @author _kisman_
 * @since 18:16 of 22.06.2022
 */
class TestCommand : Command(
    "test1"
) {
    init {
        addInstances(
            SubTestCommand1(this),
            SubTestCommand2(this)
        )
    }

    override fun runCommand(s: String, args: Array<String>) {
        runSubCommands(s, args)
    }

    override fun getDescription(): String {
        return "Just a test command"
    }

    override fun getSyntax(): String {
        return "test1 test2"
    }

    private class SubTestCommand1(instance : Command) : SubCommand("test2", instance) {
        override fun runCommand(s: String, args: Array<String>) {
            complete("Complete!!!!")
        }
    }

    private class SubTestCommand2(instance : Command) : SubCommand("test3", instance) {
        override fun runCommand(s: String, args: Array<String>) {
            error("Error(((")
        }
    }
}