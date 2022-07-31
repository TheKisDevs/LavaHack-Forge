package the.kis.devs.server.command

import me.yailya.sockets.server.SocketServerConnection
import the.kis.devs.server.command.commands.*

/**
 * @author _kisman_
 * @since 17:18 of 05.07.2022
 */
object CommandManager {
    private val commands = listOf(
        AuthCommand,
        CheckVersionCommand,
        CreateFileCommand,
        GetFileCommand,
        GetKismanClass,
        GetPublicJarCommand
    )

    fun execute(line : String, connection : SocketServerConnection) {
        if(line.isEmpty() || line.isBlank()) {
            return
        }

        val split = line.split(" ")

        for(command in commands) {
            if(command.command == split[0]) {
                command.runCommand(line, split, connection)
            }
        }
    }
}