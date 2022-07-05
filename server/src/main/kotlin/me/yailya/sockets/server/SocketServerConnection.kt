/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */

package me.yailya.sockets.server

import me.yailya.sockets.interfaces.ISocketWR
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.net.Socket
import java.net.SocketException
import kotlin.concurrent.thread

class SocketServerConnection(
    override val socket: Socket,
    private val server: SocketServer
) : ISocketWR {
    var onMessageReceived: (String) -> Unit = { }

    init {
        thread {
            while (connected) {
                val message = readString()

                if (message == null || message.isEmpty()) {
                    continue
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