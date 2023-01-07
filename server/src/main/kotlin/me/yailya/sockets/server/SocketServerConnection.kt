/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */

package me.yailya.sockets.server

import the.kis.devs.server.data.SocketMessage
import java.net.Socket
import kotlin.concurrent.thread

class SocketServerConnection(override val socket: Socket, private val server: SocketServer) : ISocketServerConnection {
    private val defaultName = "Socket-$index"

    companion object {
        private var index = -1
            get() {
                field++
                return field
            }
    }

    override var name = defaultName
    val hasCustomName get() = name != defaultName

    var onMessageReceived: (SocketMessage) -> Unit = { }

    init {
        thread {
            while (connected) {
                val message = readMessage() ?: continue

                onMessageReceived(message)
            }

            close()
        }
    }

    override fun close() {
        super.close()

        if (server.connections.contains(this)) {
            server.onSocketDisconnected(this)
            server.connections.remove(this)
        }
    }
}