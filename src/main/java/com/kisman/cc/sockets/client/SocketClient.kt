/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */
package com.kisman.cc.sockets.client

import com.kisman.cc.sockets.command.CommandManager
import com.kisman.cc.sockets.data.SocketMessage
import com.kisman.cc.sockets.interfaces.ISocketRW
import java.net.InetSocketAddress
import java.net.Socket
import java.util.*
import kotlin.concurrent.thread

@Suppress("MemberVisibilityCanBePrivate")
class SocketClient(
    private val address : String,
    private val port : Int
) : ISocketRW {
    var name = "Client-$index"

    companion object {
        var index = 0
            get() {
                field++
                return field
            }
    }

    var onSocketConnected : () -> Unit = { }
    var onSocketDisconnected : () -> Unit = { }
    var onMessageReceived : (SocketMessage) -> Unit = { }

    override val socket = Socket()

    init {
        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                socket.close()
            }
        })
    }

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

    override fun readMessage() : SocketMessage? {
        fun readMessage0(): SocketMessage? {
            val message = SocketMessage(readBytes() ?: return null)
            if(message.type == SocketMessage.Type.Text) {
                if (!CommandManager.execute(message.text!!, this)) {
                    return null
                }
            }
            return message
        }

        val message = readMessage0() ?: return null

        if(message.type == SocketMessage.Type.Text) {
            val split = message.text!!.split(" ")

            if(split.size == 2 && split[0] == "true") {
                message.text = Base64.getDecoder().decode(split[1]).toString(Charsets.UTF_8)
            }
        }

        return message
    }
}