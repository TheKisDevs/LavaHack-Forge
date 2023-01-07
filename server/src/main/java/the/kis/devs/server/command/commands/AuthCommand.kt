package the.kis.devs.server.command.commands

import the.kis.devs.server.data.SocketMessage
import the.kis.devs.server.command.Command
import the.kis.devs.server.hwid.HWID
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
 * or
 *
 * `args[0]` - name of the command
 *
 * `args[1]` - key
 *
 * `args[2]` - properties
 *
 * `args[3]` - processors
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
        return if(args.size == 3) {
            if(KeyAuthApp.keyAuth.license(args[1], args[2])) {
                listOf(SocketMessage("2"))
            } else {
                listOf(SocketMessage("1"))
            }
        } else if(args.size == 4) {
            if(KeyAuthApp.keyAuth.license(args[1], HWID(args[2], args[3].toInt()).hwid)) {
                listOf(SocketMessage("2"))
            } else {
                listOf(SocketMessage("1"))
            }
        } else listOf(SocketMessage("0"))
    }
}