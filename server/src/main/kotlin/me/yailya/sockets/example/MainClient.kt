/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */

package me.yailya.sockets.example

import me.yailya.sockets.client.SocketClient
import me.yailya.sockets.data.SocketMessage

/** Server Address - **127.0.0.1**:1234 */
const val ADDRESS = "127.0.0.1"

/** Server Port - 127.0.0.1:**1234** */
const val PORT = 1234

fun main(args: Array<String>) {
    val client = SocketClient(ADDRESS, PORT)
    client.connect()
    client.onMessageReceived = {
        if (it.type == SocketMessage.Type.Text) {
            println("Message from server: ${it.text}")
        } else {
            val file = it.file!!

            println("File from server (name: ${file.name}, description: ${file.description})")
            println(file.byteArray.toString(Charsets.UTF_8))
        }
    }

    /** Sets custom socket name if `Constants.CUSTOM_SOCKET_NAMES` is true */
    /*client.writeMessage {
        text = "LavaHack-Client"
    }*/

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
