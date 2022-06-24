package com.kisman.cc.features.module.misc.botnet.api.command

import com.kisman.cc.util.chat.other.ChatUtils

object CommandExecutor {
    fun execute(command: String) {

        val cmd = command
            .split(" ".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()

        fun getArgs() : Array<String?> {
            val args = arrayOfNulls<String>(cmd.size - 1)
            for (i in 1 until cmd.size) {
                args[i - 1] = cmd[i]
            }

            return args
        }

        for(c in BotCommandManager.commands) {
            if(c.names.contains(cmd[0])) {
                when(c.executingType) {

                    ExecutingType.NONE -> c.execute()
                    ExecutingType.ARGS -> c.execute(getArgs())
                    ExecutingType.NAMEnARGS -> c.execute(cmd[0], getArgs())
                    ExecutingType.RAW -> c.execute(command)
                }
            }
        }
    }
}