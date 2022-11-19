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
 * `1` - Invalid version
 *
 * `2|version1&version2` - Your client is latest
 *
 * @author _kisman_
 * @since 14:19 of 02.08.2022
 */
object GetVersionsCommand : Command(
    "getversions"
) {
    override fun execute(line: String, args: List<String>): List<SocketMessage> {
        if(args.size == 2) {
            var message = "2|"

            for((i, version) in versions[args[1]]!!.keys.reversed().withIndex()) {
                message += version

                if(i != versions[args[1]]!!.keys.size - 1) {
                    message += "&"
                }
            }

            //"2|b0.1.6.5-7&b0.1.6.5-6&b0.1.6.5-5&b0.1.6.5"
            return listOf(SocketMessage(message))
        }

        return listOf(SocketMessage("0"))
    }
}