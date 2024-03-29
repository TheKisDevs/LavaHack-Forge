package the.kis.devs.server.command

import the.kis.devs.server.data.SocketMessage
import the.kis.devs.server.encryption
import the.kis.devs.server.logging.CommandLogger
import the.kis.devs.server.websockets.WebSocket
import java.util.*

/**
 * @author _kisman_
 * @since 14:23 of 05.07.2022
 */
@Suppress("LeakingThis")
abstract class Command(
    val command : String
) : ICommand {
    var connection : WebSocket? = null
    val logger = CommandLogger("LavaHack Server", this)

    fun runCommand(
        line : String,
        args : List<String>,
        connection : WebSocket
    ) {
        this.connection = connection

        for(message in execute(line, args)) {
            logger.print("Answer is \"${if(message.type == SocketMessage.Type.Text) message.text else message.file?.name}\"")

            if(encryption && message.type == SocketMessage.Type.Text) {
                message.text = "true ${Base64.getEncoder().encodeToString(message.text?.toByteArray())}"
                logger.print("Encoded answer is \"${if(message.type == SocketMessage.Type.Text) message.text else message.file?.name}\"")
            }

            if(message.type == SocketMessage.Type.Text) {
                connection.send(message.text)
            } else if(message.type == SocketMessage.Type.File || message.type == SocketMessage.Type.Bytes) {
                connection.send(message.byteArray)
            }
        }
    }
}