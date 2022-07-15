/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */

package com.kisman.cc.sockets.server

import com.kisman.cc.sockets.data.SocketMessage
import com.kisman.cc.sockets.interfaces.ISocketRW
import java.net.Socket
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.thread

class SocketServerConnection(override val socket: Socket, private val server: SocketServer) : ISocketRW {
    private val defaultName = "Socket-${AtomicLong(0).get()}"

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