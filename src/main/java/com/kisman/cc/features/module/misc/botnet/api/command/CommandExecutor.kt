package com.kisman.cc.features.module.misc.botnet.api.command

object CommandExecutor {
    fun execute(command: String) {
        val cmd = command
            .split(" ".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()

        val args = arrayOfNulls<String>(cmd.size - 1)
        for (i in 1 until cmd.size) {
            args[i - 1] = cmd[i]
        }
        execute(cmd[0], args)
    }

    private fun execute(cmd: String, args: Array<String?>) {
        for(command in BotCommandManager.commands) {
            if(command.names.contains(cmd)) {
                command.execute(args)
                break
            }
        }
    }
}