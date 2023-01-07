package the.kis.devs.server.command.commands

import the.kis.devs.server.data.SocketFile
import the.kis.devs.server.data.SocketMessage
import the.kis.devs.server.DEFAULT_PATH
import the.kis.devs.server.command.Command
import java.io.File
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
 * `1` - Unable to send file
 *
 * `2` - Successful sent file
 *
 * @author _kisman_
 * @since 16:23 of 05.07.2022
 */
object GetFileCommand : Command(
    "getfile"
) {
    override fun execute(line : String, args : List<String>) : List<SocketMessage> {
        if(args.size == 2) {
            val path = DEFAULT_PATH + "/" + args[1]

            return if(Files.exists(Paths.get(path))) {
                listOf(SocketMessage("2"), SocketMessage(SocketFile(File(path))))
            } else {
                listOf(SocketMessage("1"))
            }
        }

        return listOf(SocketMessage("0"))
    }
}