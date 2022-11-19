package the.kis.devs.server.command.commands

import me.yailya.sockets.data.SocketMessage
import the.kis.devs.server.command.Command
import the.kis.devs.server.util.versions

/**
 * Arguments:
 *
 * `args[0]` - name of the command
 *
 * `args[1]` - your client's version
 *
 * Answers:
 *
 * `0` - Invalid Arguments
 *
 * `1` - Your client is outdated
 *
 * `2` - Your client is latest
 *
 * @author _kisman_
 * @since 17:06 of 05.07.2022
 */
object CheckVersionCommand : Command(
    "checkversion"
) {
    override fun execute(line: String, args: List<String>): List<SocketMessage> {
        if(args.size == 2) {
            return if(versions.containsKey(args[1])) {
                listOf(SocketMessage("2"))
            } else {
                listOf(SocketMessage("1"))
            }
        }

        return listOf(SocketMessage("0"))
    }
}