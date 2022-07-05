/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */

package me.yailya.sockets.example

import me.yailya.sockets.server.SocketServer

fun main(args: Array<String>) {
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
