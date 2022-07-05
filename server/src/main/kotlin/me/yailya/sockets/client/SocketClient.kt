/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */

package me.yailya.sockets.client

import me.yailya.sockets.interfaces.ISocketWR
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketException
import kotlin.concurrent.thread

@Suppress("MemberVisibilityCanBePrivate")
class SocketClient(
    private val address: String,
    private val port: Int
) : ISocketWR {
    var onMessageReceived: (String) -> Unit = { }

    override val socket = Socket()

    /**
     * Connects to the server
     */
    fun connect() {
        socket.connect(InetSocketAddress(address, port))

        thread {
            while (connected) {
                val message = readString()

                if (message == null || message.isEmpty()) {
                    continue
                }

                onMessageReceived(message)
            }
        }
    }
}