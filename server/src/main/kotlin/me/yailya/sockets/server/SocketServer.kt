/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */

package me.yailya.sockets.server

import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.util.*
import kotlin.concurrent.thread

class SocketServer(
    address: String,
    port: Int
) {
    private val server = ServerSocket(port, 50, InetAddress.getByName(address))
    val connections = LinkedList<SocketServerConnection>()
    var onSocketConnected: (SocketServerConnection) -> Unit = { }
    var onSocketDisconnected: (SocketServerConnection) -> Unit = { }

    val run get() = !server.isClosed

    fun start() {
        thread {
            server.use { server ->
                while (run) {
                    val socket = server.accept()

                    try {
                        val connection = SocketServerConnection(socket, this)
                        connections.add(connection)
                        onSocketConnected(connection)
                    } catch (ex: IOException) {
                        socket.close()
                    }
                }
            }
        }
    }

    fun stop() {
        connections.forEach { it.close() }
        server.close()
    }
}