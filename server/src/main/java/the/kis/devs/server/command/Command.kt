package the.kis.devs.server.command

import the.kis.devs.server.LOGGER
import the.kis.devs.server.data.SocketMessage
import the.kis.devs.server.encryption
import the.kis.devs.server.websockets.WebSocket
import the.kis.devs.server.wsNameMap
import java.util.*
import kotlin.concurrent.thread

/**
 * @author _kisman_
 * @since 14:23 of 05.07.2022
 */
abstract class Command(
    val command : String
) : ICommand {
    var connection : WebSocket? = null

    fun runCommand(
        line : String,
        args : List<String>,
        connection : WebSocket
    ) {
        this.connection = connection

        for(message in execute(line, args)) {
            LOGGER.print("Answer by command \"$command\" from web socket \"${wsNameMap[connection]}\" is \"${if(message.type == SocketMessage.Type.Text) message.text else message.file?.name}\"")

            if(encryption && message.type == SocketMessage.Type.Text) {
                message.text = "true ${Base64.getEncoder().encodeToString(message.text?.toByteArray())}"
                LOGGER.print("Encoded answer by command \"$command\" from web socket \"${wsNameMap[connection]}\" is \"${if(message.type == SocketMessage.Type.Text) message.text else message.file?.name}\"")
            }

            if(message.type == SocketMessage.Type.Text) {
                connection.send(message.text)
            } else if(message.type == SocketMessage.Type.File || message.type == SocketMessage.Type.Bytes) {
                connection.send(message.byteArray)
            }
        }
    }

    protected fun debug(
        message : String
    ) {
        LOGGER.print("Debug message by command \"$command\" from web socket \"${wsNameMap[connection]}\" is \"$message\"")
    }
}