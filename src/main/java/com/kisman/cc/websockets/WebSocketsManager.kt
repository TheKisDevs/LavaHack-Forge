package com.kisman.cc.websockets

import com.kisman.cc.Kisman
import com.kisman.cc.loader.address
import com.kisman.cc.loader.port
import com.kisman.cc.util.AccountData
import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.popupErrorDialog
import com.kisman.cc.websockets.api.client.WebSocketClient
import com.kisman.cc.websockets.api.handshake.ServerHandshake
import com.kisman.cc.websockets.command.CommandManager
import java.lang.Exception
import java.net.URI
import java.nio.ByteBuffer

/**
 * @author _kisman_
 * @since 12:12 of 05.01.2023
 */

val client = setupClient(DefaultMessageProcessor())

fun initClient() {
    Kisman.LOGGER.info("Connecting to remote server")

    client.send("addmanager ${Kisman.HASH}")

    Runtime.getRuntime().addShutdownHook(Thread {
        client.send("clientstatus ${mc.session.profile.id} 2")
        client.send("removemanager ${Kisman.HASH}")
    })

    client.send("clientstatus ${mc.session.profile.id} 1")
}

fun reportIssue(
    message : String
) {
    client.send("sendmessage Received new message: \"$message\", from \"${AccountData.key}\"")
}

fun setupClient(
    messageProcessor : IMessageProcessor
) : WebClient = WebClient(address, port, messageProcessor).also { it.connect(Thread.currentThread()) }

class WebClient(
    ip : String,
    port : Int,
    private val messageProcessor : IMessageProcessor
) : WebSocketClient(
    URI("ws://$ip:$port")
) {
    private var reconnectionTriesCount = 0

    var connected = false

    private var callerThread : Thread? = null

    fun connect(
        callerThread : Thread
    ) {
        this.callerThread = callerThread

        connect()

        callerThread.suspend()
    }

    override fun onOpen(
        handshakedata : ServerHandshake
    ) {
        Kisman.LOGGER.info("Successfully connected to remote sever!")

        connected = true

        callerThread?.resume()
    }

    override fun onMessage(
        message : String
    ) {
        messageProcessor.processMessage(message)
    }

    override fun onMessage(
        bytes : ByteBuffer
    ) {

    }

    override fun onClose(
        code : Int,
        reason : String?,
        remote : Boolean
    ) {
        connected = false

        if(code == 1000) {
            Kisman.LOGGER.info("Disconnected from remote server")
        } else {
            if (reconnectionTriesCount > 5) {
                Kisman.LOGGER.error("Stopping reconnection to remote server, shutting down!")

                popupErrorDialog("Stopping reconnection to remote server, shutting down!", true)
            } else {
                Kisman.LOGGER.error("Lost connection to remote server with code: \"$code\" and reason: \"${reason ?: "no reason"}\"")
                Kisman.LOGGER.error("Trying to reconnect!")

                popupErrorDialog("Lost connection to remote server!", false)

                connect()

                reconnectionTriesCount++
            }
        }
    }

    override fun onError(
        ex : Exception
    ) {
        if(ex.message == "Connection refused: connect") {
            Kisman.LOGGER.error("Remote server is offline, shutting down!")

            popupErrorDialog("Remote server is offline, shutting down!", true)
        } else {
            Kisman.LOGGER.error("Received error from web client", ex)
        }
    }
}

interface IMessageProcessor {
    fun processMessage(
        message : String
    )
}

class DefaultMessageProcessor : IMessageProcessor {
    override fun processMessage(
        message : String
    ) {
        CommandManager.execute(message)
    }
}