package the.kis.devs.server

import me.yailya.sockets.data.SocketMessage
import me.yailya.sockets.server.SocketServer
import the.kis.devs.server.command.CommandManager
import the.kis.devs.server.keyauth.KeyAuthApp

/**
 * @author _kisman_
 * @since 13:22 of 05.07.2022
 */

const val LATEST_CLIENT_VERSION = "1.1"
val DEFAULT_PATH = if(System.getProperty("java.class.path").contains("idea_rt.jar")) "server\\files\\server" else "./files/server"
const val LAVAHACK_CLIENT_NAME = "LavaHack-Client"
const val OP_NAME = "OP"

var ADDRESS : String? = null
var PORT = 25563

fun main(
    args : Array<String>
) {
    KeyAuthApp.keyAuth.init()

    val server = SocketServer(ADDRESS, PORT)
    server.start()
    server.onSocketConnected = { connection ->
        println("New socket connection!")

        connection.onMessageReceived = {
            if(it.type == SocketMessage.Type.Text) {
                println("Message from socket: ${it.text}")

                CommandManager.execute(it.text!!, connection)
            }
        }
    }
    server.onSocketDisconnected = {
        println("Socket disconnected!")
    }

    println("Server started with address: $ADDRESS, port: $PORT")

    while (server.run) {
        val line = readLine() ?: continue

        if (line.isEmpty()) {
            continue
        }

        if (line == "exit") {
            break
        }

        server.connections.forEach {
            it.writeMessage {
                text = line
            }
        }
    }

    server.stop()
}