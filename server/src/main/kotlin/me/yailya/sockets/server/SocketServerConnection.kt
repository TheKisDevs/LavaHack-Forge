/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */

package me.yailya.sockets.server

import me.yailya.sockets.Constants
import me.yailya.sockets.data.SocketMessage
import me.yailya.sockets.interfaces.ISocketWR
import me.yailya.sockets.utils.StackTraceUtils
import java.net.Socket
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.thread

class SocketServerConnection(
    override val socket: Socket,
    private val server: SocketServer
) : ISocketWR {
    var name = "Socket-${AtomicLong(0).get()}"

    private var haveCustomName = !Constants.CUSTOM_SOCKET_NAMES

    var onMessageReceived: (SocketMessage) -> Unit = { }

    init {
        socket.sendBufferSize = Constants.BUFFER_SIZE
        socket.receiveBufferSize = Constants.BUFFER_SIZE
    }

    init {
        thread {
            while (connected) {
                val message = readMessage() ?: continue

                if(!haveCustomName && message.type == SocketMessage.Type.Text) {
                    name = message.text
                }

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