/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */

package me.yailya.sockets.example

import me.yailya.sockets.client.SocketClient

/** Server Address - **127.0.0.1**:1234 */
const val ADDRESS = "127.0.0.1"
/** Server Port - 127.0.0.1:**1234** */
const val PORT = 1234

fun main(args: Array<String>) {
    val client = SocketClient(ADDRESS, PORT)
    client.connect()
    client.onMessageReceived = {
        println("Message from server: $it")
    }

    while (client.connected) {
        val line = readLine() ?: continue

        if (line.isEmpty()) {
            continue
        }

        client.writeString(line)

        if (line == "exit") {
            break
        }
    }
}
