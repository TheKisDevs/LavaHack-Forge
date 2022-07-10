/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */

package me.yailya.sockets.client

import me.yailya.sockets.Constants
import me.yailya.sockets.data.SocketMessage
import me.yailya.sockets.interfaces.ISocketWR
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.concurrent.thread

@Suppress("MemberVisibilityCanBePrivate")
class SocketClient(
    private val address: String,
    private val port: Int
) : ISocketWR {
    var onMessageReceived: (SocketMessage) -> Unit = { }
    var onSocketDisconnected: () -> Unit = { }

    override val socket = Socket()

    init {
        socket.sendBufferSize = Constants.BUFFER_SIZE
        socket.receiveBufferSize = Constants.BUFFER_SIZE
    }

    /**
     * Connects to the server
     */
    fun connect() {
        socket.connect(InetSocketAddress(address, port))

        thread {
            while (connected) {
                onMessageReceived(readMessage() ?: continue)
            }

            onSocketDisconnected()
        }
    }
}