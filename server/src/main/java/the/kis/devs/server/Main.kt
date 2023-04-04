package the.kis.devs.server

import the.kis.devs.server.command.CommandManager
import the.kis.devs.server.emulate.EmulateWebSocket
import the.kis.devs.server.keyauth.KeyAuthApp
import the.kis.devs.server.logging.Logger
import the.kis.devs.server.websocket.IMessageProcessor
import the.kis.devs.server.websocket.WebServer
import the.kis.devs.server.websockets.WebSocket
import the.kis.devs.server.websockets.handshake.ClientHandshake
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

/**
 * @author _kisman_
 * @since 13:22 of 05.07.2022
 */

val DEFAULT_PATH = if(System.getProperty("java.class.path").contains("idea_rt.jar")) "server\\files\\server" else "./files/server"
val LOGS_PATH = "$DEFAULT_PATH/logs"

var ADDRESS : String? = null
var PORT = 25563

var encryption = false

var server : WebServer? = null

val emulateSocket = EmulateWebSocket()

//I am adding it cuz i dont wanna to rewrite the lib
val wsNameMap = HashMap<WebSocket, String>()
val wsManagerMap = ConcurrentHashMap<String, WebSocket>()

val now = LocalDateTime.now()!!

val LOGGER = Logger("LavaHack Logger")

fun main(
    args : Array<String>
) {
    if(args.size == 1) {
        PORT = Integer.valueOf(args[0])
    }

    if(!Files.exists(Paths.get(LOGS_PATH))) {
        Files.createDirectory(Paths.get(LOGS_PATH))
    }

    var index = 0

    wsNameMap[emulateSocket] = "WebSocket-Emulation"

    val messageProcessor = object : IMessageProcessor {
        override fun onOpen(
            conn : WebSocket?,
            handshake : ClientHandshake?
        ) {
            wsNameMap[conn!!] = "WebSocket-$index"

            index++

            LOGGER.print("Web socket \"${wsNameMap[conn]}\" connected!")
        }

        override fun onClose(
            conn : WebSocket?,
            code : Int,
            reason : String?,
            remote : Boolean
        ) {
            LOGGER.print("Web socket \"${wsNameMap[conn]}\" disconnected!")
        }

        override fun onMessage(
            conn : WebSocket?,
            message : String
        ) {
            LOGGER.print("Message from web socket \"${wsNameMap[conn]}\" is \"${message}\"")

            CommandManager.execute(message, conn!!)
        }

    }

    server = WebServer(PORT, messageProcessor)

    KeyAuthApp.keyAuth.init()

    server!!.start()

    LOGGER.print("> Server started with address: $ADDRESS, port: $PORT")

    var emulating = false

    while (true) {
        val line = readLine() ?: continue

        if (line.isEmpty()) {
            continue
        }

        if(emulating) {
            LOGGER.print("> Emulating \"$line\" message!")
            CommandManager.execute(line, emulateSocket)
            LOGGER.print("> Leaving emulating mode!")
            emulating = false
            continue
        }

        if (line == "exit") {
            break
        } else if(line == "encryption true") {
            encryption = true
            LOGGER.print("> Encryption is true")
        } else if(line == "encryption false") {
            encryption = false
            LOGGER.print("> Encryption is false")
        } else if(line == "encryption status") {
            LOGGER.print("> Encryption is $encryption")
        } else if(line == "emulate") {
            emulating = true
            LOGGER.print("> Turned on emulating mode")
        } else if(line == "getmanagers") {
            if(!wsManagerMap.isEmpty()) {
                LOGGER.print("> Current managers:")

                for (manager in wsManagerMap.keys) {
                    LOGGER.print("> > Manager: $manager")
                }
            } else {
                LOGGER.print("> No managers :<")
            }
        } else if(line == "help") {
            LOGGER.print(
                """> Commands:
> > exit - stops the server
> > encryption <true/false> - changes state of encryption
> > encryption status - shows current value of "encryption" field
> > help - shows this menu
> > message <text> - sends <text> to all connections
> > emulate - starts command emulating mode"""
            )
        } else if(line.startsWith("message ")) {
            sendMessage(line.removePrefix("message "))
        }
    }

    server!!.stop()
}

fun sendMessage(
    message : String
) {
    server!!.broadcast(message, wsManagerMap.values)
}