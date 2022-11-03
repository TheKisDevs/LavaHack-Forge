package com.kisman.cc.sockets.command.commands

import com.kisman.cc.features.capes.CapeManager
import com.kisman.cc.sockets.command.Command
import com.kisman.cc.sockets.data.SocketMessage

/**
 * `args[1]` - clear
 *
 * or
 *
 * `args[1]` - add/remove
 *
 * `args[2]` - name of the cape
 *
 * `args[3]` - uuid
 *
 * @author _kisman_
 * @since 14:56 of 01.11.2022
 */
object CapeCommand : Command(
    "cape"
) {
    override fun execute(
        line : String,
        args : List<String>
    ): List<SocketMessage> {
        when(args.size) {
            2 -> {
                if(args[1] == "clear") {
                    CapeManager.clear()
                }
            }
            4 -> {
                when(args[1]) {
                    "add" -> {
                        CapeManager.add(args[3], CapeManager.getCape(args[2]))
                    }
                    "remove" -> {
                        CapeManager.remove(args[3])
                    }
                }
            }
        }

        return emptyList()
    }
}