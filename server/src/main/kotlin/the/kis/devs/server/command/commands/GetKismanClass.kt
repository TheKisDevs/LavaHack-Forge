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
 * @author _kisman_
 * @since 22:45 of 31.07.2022
 */
object GetKismanClass : Command(
    "getkismanclass"
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
                    val getFileAnswer = GetFileCommand.execute("", listOf("getfile", "kismanClass\\kismanClass.jar"))

                    if(getFileAnswer[0].text == "2") {
                        return listOf(SocketMessage("2"), SocketMessage(SocketFile(File("$DEFAULT_PATH\\publicJar\\publicJar.jar"), "LavaHack")))
                    }
                }
            }

            return listOf(SocketMessage("1"))
        }

        if(args.size == 1) {
            return listOf(SocketMessage("2"), SocketMessage(SocketFile(File("$DEFAULT_PATH\\kismanClass\\kismanClass.jar"), "KismanClass")))
        }

        return listOf(SocketMessage("0"))
    }
}