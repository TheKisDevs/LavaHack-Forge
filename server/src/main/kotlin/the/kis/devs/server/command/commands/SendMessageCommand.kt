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
        println(args.joinToString(" "))

        //TODO: discord bot implementation

        return emptyList()
    }
}