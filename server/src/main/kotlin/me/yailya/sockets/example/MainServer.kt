/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */

package me.yailya.sockets.example

import me.yailya.sockets.data.SocketMessage
import me.yailya.sockets.server.SocketServer

fun main(args: Array<String>) {
    val server = SocketServer(ADDRESS, PORT)
    server.start()
    server.onSocketConnected = { connection ->
        println("New connection!")

        connection.onMessageReceived = {
            if (it.type == SocketMessage.Type.Text) {
                println("Message from client: ${it.text}")
            } else {
                val file = it.file!!

                println("File from client (name: ${file.name}, description: ${file.description})")
                println(file.byteArray.toString(Charsets.UTF_8))
            }
        }
    }
    server.onSocketDisconnected = {
        println("Client disconnected!")
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

        server.connections.forEach {
            it.writeMessage {
                text = line
            }
        }
    }
}