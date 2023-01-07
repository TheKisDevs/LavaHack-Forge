/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */

package me.yailya.sockets.client

import the.kis.devs.server.data.SocketMessage
import me.yailya.sockets.interfaces.ISocketRW
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.concurrent.thread

@Suppress("MemberVisibilityCanBePrivate")
class SocketClient(private val address: String, private val port: Int) : ISocketRW {
    var onSocketConnected: () -> Unit = { }
    var onSocketDisconnected: () -> Unit = { }
    var onMessageReceived: (SocketMessage) -> Unit = { }

    override val socket = Socket()

    /**
     * Connects to the server
     */
    fun connect() {
        socket.connect(InetSocketAddress(address, port))

        thread {
            onSocketConnected()

            while (connected) {
                onMessageReceived(readMessage() ?: continue)
            }

            onSocketDisconnected()
        }
    }
}