package the.kis.devs.server

import me.yailya.sockets.example.ADDRESS
import me.yailya.sockets.example.PORT
import me.yailya.sockets.server.SocketServer

/**
 * @author _kisman_
 * @since 13:22 of 05.07.2022
 */


fun main() {
    val server = SocketServer(ADDRESS, PORT)
    server.start()
    server.onSocketConnected = { connection ->
        println("New socket connection!")

        connection.onMessageReceived = {
            println("Message from socket: $it")
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
            server.stop()
        }

        server.connections.forEach { it.writeString(line) }
    }
}