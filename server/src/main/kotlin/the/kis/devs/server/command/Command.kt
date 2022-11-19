package the.kis.devs.server.command

import me.yailya.sockets.data.SocketMessage
import me.yailya.sockets.server.ISocketServerConnection
import me.yailya.sockets.server.SocketServerConnection
import the.kis.devs.server.encryption
import the.kis.devs.server.permission.IPermission
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

/**
 * @author _kisman_
 * @since 14:23 of 05.07.2022
 */
abstract class Command(
    val command : String
) : ICommand {
    val permissions = ArrayList<IPermission>()
    var connection : ISocketServerConnection? = null

    fun runCommand(line : String, args : List<String>, connection : ISocketServerConnection) {
        this.connection = connection

//        thread {
            for(message in execute(line, args)) {
                println("Answer by command \"$command\" from socket \"${connection.name}\" is \"${if(message.type == SocketMessage.Type.Text) message.text else message.file?.name}\"")

                if(encryption && message.type == SocketMessage.Type.Text) {
                    message.text = "true ${Base64.getEncoder().encodeToString(message.text?.toByteArray())}"
                    println("Encoded answer by command \"$command\" from socket \"${connection.name}\" is \"${if(message.type == SocketMessage.Type.Text) message.text else message.file?.name}\"")
                }

                connection.writeMessage(message)
            }
//        }
    }
}