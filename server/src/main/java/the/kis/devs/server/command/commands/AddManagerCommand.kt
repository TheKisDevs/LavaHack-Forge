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
 * `2` - successfully added new manager
 *
 * `*nothing* - idk
 *
 * @author _kisman_
 * @since 16:04 of 07.01.2023
 */
object AddManagerCommand : Command(
    "addmanager"
) {
    override fun execute(
        line : String,
        args : List<String>
    ) : List<SocketMessage> {
        if(args.size == 2 || args.size == 3) {
            wsManagerMap[args[1]] = connection!!

            if(args.size == 3) {
                return listOf(SocketMessage("2"))
            }

            return emptyList()
        }

        return listOf(SocketMessage("0"))
    }
}