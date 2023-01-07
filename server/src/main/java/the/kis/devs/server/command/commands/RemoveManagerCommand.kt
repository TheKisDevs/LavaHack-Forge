package the.kis.devs.server.command.commands

import the.kis.devs.server.command.Command
import the.kis.devs.server.data.SocketMessage
import the.kis.devs.server.wsManagerMap

/**
 * Arguments:
 *
 * `args[0]` - name of the command
 *
 * `args[1]` - name of the web socket
 *
 * `args[2]` - can we answer?
 *
 * Answers:
 *
 * `0` - not enough arguments
 *
 * `1` - we dont have this web socket
 *
 * `2` - successfully removed manager
 *
 * `*nothing* - idk
 *
 * @author _kisman_
 * @since 16:17 of 07.01.2023
 */
object RemoveManagerCommand : Command(
    "removemanager"
) {
    override fun execute(
        line : String,
        args : List<String>
    ) : List<SocketMessage> {
        if(args.size == 2 || args.size == 3) {
            val flag = wsManagerMap.contains(args[1])

            if(flag) {
                wsManagerMap.remove(args[1])
            }

            if(args.size == 3) {
                return listOf(SocketMessage(if(flag) "2" else "1"))
            }

            return emptyList()
        }

        return listOf(SocketMessage("0"))
    }
}