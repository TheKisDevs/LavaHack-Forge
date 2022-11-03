package com.kisman.cc.sockets.command

import com.kisman.cc.sockets.client.SocketClient
import com.kisman.cc.sockets.command.commands.CapeCommand

/**
 * @author _kisman_
 * @since 17:18 of 05.07.2022
 */
object CommandManager {
    private val commands = listOf(
        CapeCommand
    )

    fun execute(
        line : String,
        client : SocketClient
    ) : Boolean {
        if(line.isEmpty() || line.isBlank()) {
            return false
        }

        val split = line.split(" ")

        var flag = false

        for(command in commands) {
            if(command.command == split[0]) {
                command.runCommand(line, split, client)
                flag = true
            }
        }

        return flag
    }
}