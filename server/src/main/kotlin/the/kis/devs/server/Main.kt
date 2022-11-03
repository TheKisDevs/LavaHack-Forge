package the.kis.devs.server

import me.yailya.sockets.data.SocketMessage
import me.yailya.sockets.server.SocketServer
import the.kis.devs.server.command.CommandManager
import the.kis.devs.server.keyauth.KeyAuthApp

/**
 * @author _kisman_
 * @since 13:22 of 05.07.2022
 */

const val LATEST_CLIENT_VERSION = "1.3"
val DEFAULT_PATH = if(System.getProperty("java.class.path").contains("idea_rt.jar")) "server\\files\\server" else "./files/server"
const val LAVAHACK_CLIENT_NAME = "LavaHack-Client"
const val OP_NAME = "OP"

var ADDRESS : String? = null
var PORT = 25563

var encryption = false

var server : SocketServer? = null

fun main(
    args : Array<String>
) {
    if(args.size == 1) {
        PORT = Integer.valueOf(args[0])
    }

    server = SocketServer(ADDRESS, PORT)

    KeyAuthApp.keyAuth.init()

    server!!.start()
    server!!.onSocketConnected = { connection ->
        println("Socket \"${connection.name}\" connected!")

        connection.onMessageReceived = {
            if(it.type == SocketMessage.Type.Text) {
                println("Message from socket \"${connection.name}\" is \"${it.text}\"")

                CommandManager.execute(it.text!!, connection)
            }
        }
    }
    server!!.onSocketDisconnected = { connection ->
        println("Socket \"${connection.name}\" disconnected!")
    }

    println("> Server started with address: $ADDRESS, port: $PORT")

    while (server!!.run) {
        val line = readLine() ?: continue

        if (line.isEmpty()) {
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
        } else if(line == "help") {
            println(
                """> Commands:
                > > exit - stops the server
                > > encryption <true/false> - changes state of encryption
                > > encryption status - shows current value of "encryption" field
                > > help - shows this menu
                > > message <text> - sends <text> to add connections"""
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
    server!!.connections.forEach {
        it.writeMessage {
            text = message
        }
    }
}