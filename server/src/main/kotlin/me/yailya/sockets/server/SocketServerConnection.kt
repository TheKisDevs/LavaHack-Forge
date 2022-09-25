/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */

package me.yailya.sockets.server

import me.yailya.sockets.data.SocketMessage
import me.yailya.sockets.interfaces.ISocketRW
import java.net.Socket
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.thread

class SocketServerConnection(override val socket: Socket, private val server: SocketServer) : ISocketRW {
    private val defaultName = "Socket-$index"

    companion object {
        private var index = -1
            get() {
                field++
                return field
            }
    }

    var name = defaultName
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