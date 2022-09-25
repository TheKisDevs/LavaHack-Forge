package the.kis.devs.server.command.commands

import me.yailya.sockets.data.SocketMessage
import the.kis.devs.server.command.Command
import the.kis.devs.server.command.CommandManager
import java.nio.charset.Charset
import java.util.Base64

/**
 * @author _kisman_
 * @since 11:08 of 24.09.2022
 */
object TrueCommand : Command(
    "true"
) {
    override fun execute(
        line : String,
        args : List<String>
    ) : List<SocketMessage> {
        if(args.size == 2) {
            val decoded = Base64.getDecoder().decode(args[1]).toString(Charsets.UTF_8)

            println("Decoded message from socket \"${connection?.name}\": $decoded")

            CommandManager.execute(decoded, connection!!)

            return listOf()
        }

        return listOf(SocketMessage("-1"))
    }
}