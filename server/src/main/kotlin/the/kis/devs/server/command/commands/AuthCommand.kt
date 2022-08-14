package the.kis.devs.server.command.commands

import me.yailya.sockets.data.SocketMessage
import the.kis.devs.server.command.Command
import the.kis.devs.server.keyauth.KeyAuthApp

/**
 * Arguments:
 *
 * `args[0]` - name of the command
 *
 * `args[1]` - key
 *
 * `args[2]` - hwid
 *
 * Answers:
 *
 * `0` - Invalid Arguments
 *
 * `1` - Invalid Key or HWID
 *
 * `2` - Valid Key and HWID
 *
 * @author _kisman_
 * @since 14:20 of 05.07.2022
 */
object AuthCommand : Command(
    "auth"
) {
    override fun execute(line: String, args: List<String>) : List<SocketMessage> {
        if(args.size == 3) {
            return if(KeyAuthApp.keyAuth.license(args[1], args[2])) {
                listOf(SocketMessage("2"))
            } else {
                listOf(SocketMessage("1"))
            }
        }

        return listOf(SocketMessage("0"))
    }
}