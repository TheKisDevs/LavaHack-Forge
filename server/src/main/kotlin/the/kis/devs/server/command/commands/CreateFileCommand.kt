package the.kis.devs.server.command.commands

import me.yailya.sockets.data.SocketMessage
import me.yailya.sockets.server.SocketServerConnection
import the.kis.devs.server.DEFAULT_PATH
import the.kis.devs.server.OP_NAME
import the.kis.devs.server.command.Command
import the.kis.devs.server.permission.IPermission
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Arguments:
 *
 * `args[0]` - name of the command
 *
 * `args[1]` - file name
 *
 * Answers:
 *
 * `0` - Invalid Arguments
 *
 * `1` - Unable to create file
 *
 * `2` - Successful created file
 *
 * @author _kisman_
 * @since 16:11 of 05.07.2022
 */
object CreateFileCommand : Command(
    "createfile"
) {
    init {
        permissions.add(
            object : IPermission {
                override fun check(connection: SocketServerConnection): Boolean {
                    return connection.name == OP_NAME
                }
            }
        )
    }

    override fun execute(line: String, args: List<String>): List<SocketMessage> {
        if(args.size == 2) {
            return try {
                Files.createFile(
                    Paths.get(
                        "$DEFAULT_PATH\\${args[1]}"
                    )
                )

                listOf(SocketMessage("2"))
            } catch(e : Exception) {
                e.printStackTrace()
                listOf(SocketMessage("1"))
            }
        }

        return listOf(SocketMessage("0"))
    }
}