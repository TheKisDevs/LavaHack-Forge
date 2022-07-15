/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */

package com.kisman.cc.sockets.example

import com.kisman.cc.sockets.client.SocketClient
import com.kisman.cc.sockets.data.SocketMessage

/** Server Address - **127.0.0.1**:1234 */
const val ADDRESS = "127.0.0.1"

/** Server Port - 127.0.0.1:**1234** */
const val PORT = 1234

fun main() {
    val client = SocketClient(ADDRESS, PORT)

    client.onMessageReceived = {
        when (it.type) {
            SocketMessage.Type.Text -> {
                println("Text from server: ${it.text}")
            }
            SocketMessage.Type.File -> {
                val file = it.file!!

                println("File from server (name: ${file.name}, description: ${file.description}, size: ${file.byteArray.size})")
            }
            SocketMessage.Type.Bytes -> {
                println("Bytes from server (size: ${it.byteArray.size})")
            }
        }
    }

    client.connect()

    if (CUSTOM_SOCKET_NAMES) {
        client.writeMessage {
            text = "ExampleSocketName"
        }
    }

    while (client.connected) {
        val line = readLine() ?: continue

        if (line.isEmpty()) {
            continue
        }

        client.writeMessage {
            text = line
        }

        if (line == "exit") {
            break
        }
    }
}
