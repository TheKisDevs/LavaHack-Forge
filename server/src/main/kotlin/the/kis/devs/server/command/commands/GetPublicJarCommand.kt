package the.kis.devs.server.command.commands

import me.yailya.sockets.data.SocketFile
import me.yailya.sockets.data.SocketMessage
import me.yailya.sockets.server.SocketServerConnection
import the.kis.devs.server.DEFAULT_PATH
import the.kis.devs.server.LAVAHACK_CLIENT_NAME
import the.kis.devs.server.OP_NAME
import the.kis.devs.server.command.Command
import the.kis.devs.server.permission.IPermission
import java.io.File

/**
 * Arguments:
 *
 * `args[0]` - name of the command
 *
 * `args[1]` - Key
 *
 * `args[2]` - Client version
 *
 * `args[3]` - Values for HWID generation
 *
 * `args[4]` - Available Processors for HWID generation
 *
 * Answers:
 *
 * `0` - Invalid Arguments
 *
 * `1` - Invalid Key or HWID | Client is outdated
 *
 * `2` - Valid Key, HWID, Client is latest
 *
 * @author _kisman_
 * @since 17:33 of 05.07.2022
 */
object GetPublicJarCommand : Command(
    "getpubluicjar"
) {
    init {
        permissions.add(
            object : IPermission {
                override fun check(connection: SocketServerConnection): Boolean {
                    return connection.name == LAVAHACK_CLIENT_NAME || connection.name == OP_NAME
                }
            }
        )
    }

    override fun execute(line: String, args: List<String>): List<SocketMessage> {
        if(args.size == 5) {
            val authAnswer = AuthCommand.execute("", listOf("auth", args[1], args[2]))

            if(authAnswer[0].text == "2") {
                val checkVersionAnswer = CheckVersionCommand.execute("", listOf("checkversion", args[3]))

                if(checkVersionAnswer[0].text == "2") {
                    val getFileAnswer = GetFileCommand.execute("", listOf("getfile", "publicJar\\publicJar.jar"))

                    if(getFileAnswer[0].text == "2") {
                        return listOf(SocketMessage("2"), SocketMessage(SocketFile(File("$DEFAULT_PATH\\publicJar\\publicJar.jar"), "CANT_BE_OPENED")))
                    }
                }
            }

            return listOf(SocketMessage("1"))
        }

        return listOf(SocketMessage("0"))
    }
}