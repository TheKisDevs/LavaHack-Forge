package the.kis.devs.server.command.commands

import me.yailya.sockets.data.SocketFile
import me.yailya.sockets.data.SocketMessage
import the.kis.devs.server.command.Command
import java.io.File

/**
 * @author _kisman_
 * @since 15:37 of 31.08.2022
 */
object GetLibrariesCommand : Command(
    "getlibraries"
) {
    override fun execute(
        line : String,
        args : List<String>
    ) : List<SocketMessage> {
        val folder = File("files/server/libraries")

        val messages = mutableListOf<SocketMessage>()

        for(file in folder.listFiles()!!) {
            messages.add(SocketMessage(SocketFile(file.name, file.readBytes(), "library")))
        }

        messages.add(SocketMessage(folder.listFiles()!!.size.toString()))

        return messages
    }
}