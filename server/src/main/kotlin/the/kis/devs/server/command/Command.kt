package the.kis.devs.server.command

import me.yailya.sockets.server.SocketServerConnection

/**
 * @author _kisman_
 * @since 14:23 of 05.07.2022
 */
abstract class Command(
    val command : String
) : ICommand {
    private var line = ""
    private var args = emptyList<String>()
    private var connection : SocketServerConnection? = null

    fun runCommand(line : String, args : List<String>, connection : SocketServerConnection) {
        this.line = line
        this.args = args
        this.connection = connection
        Thread {
            connection.writeString(execute(line, args))
        }.start()
    }
}