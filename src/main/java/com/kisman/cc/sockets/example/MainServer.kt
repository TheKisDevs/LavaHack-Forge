/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */

package com.kisman.cc.sockets.example

import com.kisman.cc.sockets.data.SocketMessage
import com.kisman.cc.sockets.server.SocketServer

const val CUSTOM_SOCKET_NAMES = false

fun main() {
    val server = SocketServer(ADDRESS, PORT)

    server.onSocketConnected = { connection ->
        println("New connection!")

        connection.onMessageReceived = onMessageReceived@{
            when (it.type) {
                SocketMessage.Type.Text -> {
                    if (CUSTOM_SOCKET_NAMES && !connection.hasCustomName) {
                        connection.name = it.text!!

                        return@onMessageReceived
                    }

                    println("Text from client: ${it.text}")
                }
                SocketMessage.Type.File -> {
                    val file = it.file!!

                    println("File from client (name: ${file.name}, description: ${file.description}, size: ${file.byteArray.size})")
                }
                SocketMessage.Type.Bytes -> {
                    println("Bytes from client (size: ${it.byteArray.size})")
                }
            }
        }
    }

    server.onSocketDisconnected = {
        println("Client disconnected")
    }

    server.onStart = {
        println("Server started with address: $ADDRESS, port: $PORT")
    }

    server.onStop = {
        println("Server stopped")
    }

    server.start()

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