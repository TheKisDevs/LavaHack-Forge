/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */

package me.yailya.sockets.server

import the.kis.devs.server.util.Constants
import java.net.InetAddress
import java.net.ServerSocket
import java.util.*
import kotlin.concurrent.thread

class SocketServer(
    address : String?,
    port : Int
) {
    private val server = ServerSocket(port, Constants.SERVER_BACKLOG, (if(address == null) null else InetAddress.getByName(address)))

    val connections = LinkedList<SocketServerConnection>()
    var onSocketConnected: (SocketServerConnection) -> Unit = { }
    var onSocketDisconnected: (SocketServerConnection) -> Unit = { }
    var onStart: () -> Unit = { }
    var onStop: () -> Unit = { }

    val run get() = !server.isClosed

    var stopped = false

    fun start() {
        thread {
            onStart()

            while (run) {
                if(stopped) {
                    return@thread
                }

                val socket = server.accept()

                try {
                    val connection = SocketServerConnection(socket, this)
                    connections.add(connection)
                    onSocketConnected(connection)
                } catch (ex: Exception) {
                    ex.printStackTrace()

                    socket.close()
                }
            }

            stop()
        }
    }

    fun stop() {
        onStop()
        connections.forEach { it.close() }
        server.close()

        stopped = true
    }
}