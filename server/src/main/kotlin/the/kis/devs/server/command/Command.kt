package the.kis.devs.server.command

import me.yailya.sockets.data.SocketMessage
import me.yailya.sockets.server.SocketServerConnection
import the.kis.devs.server.permission.IPermission

/**
 * @author _kisman_
 * @since 14:23 of 05.07.2022
 */
abstract class Command(
    val command : String
) : ICommand {
    val permissions = ArrayList<IPermission>()

    fun runCommand(line : String, args : List<String>, connection : SocketServerConnection) {
        Thread {
            for(message in execute(line, args)) {
                println("Answer by command $command is ${if(message.type == SocketMessage.Type.Text) message.text else message.file?.name}")
                connection.writeMessage(message)
            }
        }.start()
    }
}