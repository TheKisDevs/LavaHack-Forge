package the.kis.devs.server.command.commands

import the.kis.devs.server.data.SocketFile
import the.kis.devs.server.data.SocketMessage
import the.kis.devs.server.DEFAULT_PATH
import the.kis.devs.server.command.Command
import the.kis.devs.server.hwid.HWID
import the.kis.devs.server.util.versions
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
 * `args[5]` - Version of LavaHack
 *
 * Answers:
 *
 * `0` - Invalid Arguments
 *
 * `1` - Invalid Key or HWID or Client is outdated
 *
 * `2` - Valid Key, HWID, Client is latest
 *
 * `3` - Illegal access for selected version
 *
 * `4` - Wrong filter
 *
 * @author _kisman_
 * @since 17:33 of 05.07.2022
 */
object GetPublicJarCommand : Command(
    "getpublicjar"
) {
    override fun execute(line: String, args: List<String>): List<SocketMessage> {
        if(args.size == 6) {
            for(property in args[3].split("|")) {
                try {
                    val split = property.split("&")
                    val name = split[0]
                    val value = split[1]

                    if (name == "sun.jvm.hotspot.tools.jcore.filter" && value != "com.kisman.cc.loader.antidump.MaliciousClassFilter") {
                        return listOf(SocketMessage("4"))
                    }
                } catch(_ : Exception) { }
            }

            val authAnswer = AuthCommand.execute("", listOf("auth", args[1], HWID(args[3], args[4].toInt()).hwid))//TODO: need try catch

            debug("Auth command answer is ${authAnswer[0].text}")

            if(authAnswer[0].text == "2") {
                val checkVersionAnswer = CheckVersionCommand.execute("", listOf("checkversion", args[2]))

                debug("CheckVersion command answer is ${checkVersionAnswer[0].text}")

                if(checkVersionAnswer[0].text == "2") {
                    val versionIndex = args[5].replace("_", " ")
                    val versionFile = versions[args[2]]?.get(versionIndex)

                    val getFileAnswer = GetFileCommand.execute("", listOf("getfile", "publicJar/$versionFile"))

                    println("GetFile command answer is ${getFileAnswer[0].text}")

                    if(getFileAnswer[0].text == "2") {

                        return listOf(SocketMessage("2"), SocketMessage(SocketFile(File("$DEFAULT_PATH/publicJar/$versionFile"))))
                    }
                }
            }

            return listOf(SocketMessage("1"))
        }

        return listOf(SocketMessage("0"))
    }
}