/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */

package com.kisman.cc.loader.sockets.client

import com.kisman.cc.loader.sockets.data.SocketMessage
import com.kisman.cc.loader.sockets.interfaces.ISocketRW
import java.net.InetSocketAddress
import java.net.Socket
import java.util.*
import kotlin.concurrent.thread

@Suppress("MemberVisibilityCanBePrivate")
class SocketClient(
    private val address : String,
    private val port : Int
) : ISocketRW {
    var onSocketConnected : () -> Unit = { }
    var onSocketDisconnected : () -> Unit = { }
    var onMessageReceived : (SocketMessage) -> Unit = { }

    override val socket = Socket()

    /**
     * Connects to the server
     */
    fun connect() {
        socket.connect(InetSocketAddress(address, port))

        Runtime.getRuntime().addShutdownHook(Thread {
            if(connected) {
                close()
            }
        });

        thread {
            onSocketConnected()

            while (connected) {
                onMessageReceived(readMessage() ?: continue)
            }

            onSocketDisconnected()
        }
    }

    override fun readMessage() : SocketMessage? {
        val message = super.readMessage() ?: return null

        if(message.type == SocketMessage.Type.Text) {
            val split = message.text!!.split(" ")

            if(split.size == 2 && split[0] == "true") {
                message.text = Base64.getDecoder().decode(split[1]).toString(Charsets.UTF_8)
            }
        }

        return message
    }
}