package the.kis.devs.server.command.commands

import me.yailya.sockets.data.SocketMessage
import the.kis.devs.server.command.Command

/**
 * @author _kisman_
 * @since 22:03 of 03.09.2022
 */
object SendMessageCommand : Command(
    "sendmessage"
) {
    override fun execute(
        line : String,
        args : List<String>
    ) : List<SocketMessage> {
        if(args.size == 1) {
            println(args[0])

            //TODO: discord bot implementation
        }

        return emptyList()
    }
}