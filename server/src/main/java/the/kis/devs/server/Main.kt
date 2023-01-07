package the.kis.devs.server

import the.kis.devs.server.command.CommandManager
import the.kis.devs.server.emulate.EmulateWebSocket
import the.kis.devs.server.keyauth.KeyAuthApp
import the.kis.devs.server.websocket.IMessageProcessor
import the.kis.devs.server.websocket.WebServer
import the.kis.devs.server.websockets.WebSocket
import the.kis.devs.server.websockets.handshake.ClientHandshake
import java.util.concurrent.ConcurrentHashMap

/**
 * @author _kisman_
 * @since 13:22 of 05.07.2022
 */

val DEFAULT_PATH = if(System.getProperty("java.class.path").contains("idea_rt.jar")) "server\\files\\server" else "./files/server"

var ADDRESS : String? = null
var PORT = 25563

var encryption = false

var server : WebServer? = null

val emulateSocket = EmulateWebSocket()

//I am adding it cuz i dont wanna to rewrite the lib
val wsNameMap = HashMap<WebSocket, String>()
val wsManagerMap = ConcurrentHashMap<String, WebSocket>()

fun main(
    args : Array<String>
) {
    if(args.size == 1) {
        PORT = Integer.valueOf(args[0])
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

            println("Web socket \"${wsNameMap[conn]}\" connected!")
        }

        override fun onClose(
            conn : WebSocket?,
            code : Int,
            reason : String?,
            remote : Boolean
        ) {
            println("Web socket \"${wsNameMap[conn]}\" disconnected!")
        }

        override fun onMessage(
            conn : WebSocket?,
            message : String
        ) {
            println("Message from web socket \"${wsNameMap[conn]}\" is \"${message}\"")

            CommandManager.execute(message, conn!!)
        }

    }

    server = WebServer(PORT, messageProcessor)

    KeyAuthApp.keyAuth.init()

    server!!.start()

    println("> Server started with address: $ADDRESS, port: $PORT")

    var emulating = false

    while (true) {
        val line = readLine() ?: continue

        if (line.isEmpty()) {
            continue
        }

        if(emulating) {
            println("> Emulating \"$line\" message!")
            CommandManager.execute(line, emulateSocket)
            println("> Leaving emulating mode!")
            emulating = false
            continue
        }

        if (line == "exit") {
            break
        } else if(line == "encryption true") {
            encryption = true
            println("> Encryption is true")
        } else if(line == "encryption false") {
            encryption = false
            println("> Encryption is false")
        } else if(line == "encryption status") {
            println("> Encryption is $encryption")
        } else if(line == "emulate") {
            emulating = true
            println("> Turned on emulating mode")
        } else if(line == "help") {
            println(
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