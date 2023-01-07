package com.kisman.cc.loader.websockets

import com.kisman.cc.loader.LavaHackLoaderCoreMod
import com.kisman.cc.loader.Utility
import com.kisman.cc.loader.address
import com.kisman.cc.loader.port
import com.kisman.cc.loader.websockets.api.client.WebSocketClient
import com.kisman.cc.loader.websockets.api.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI
import java.nio.ByteBuffer

/**
 * @author _kisman_
 * @since 12:12 of 05.01.2023
 */

var client : WebClient? = null

val DUMMY_MESSAGE_PROCESSOR = DummyMessageProcessor()

fun initClient() {
    LavaHackLoaderCoreMod.LOGGER.info("Connecting to remove server")

    client = setupClient(DefaultMessageProcessor())
}

fun reportIssue(
    message : String
) {
    client?.send("sendmessage Received new message: \"$message\"")
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
        LavaHackLoaderCoreMod.LOGGER.info("Successfully connected to remote sever!")

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
        messageProcessor.processMessage(bytes)
    }

    override fun onClose(
        code : Int,
        reason : String?,
        remote : Boolean
    ) {
        connected = false

        if(code == 1000) {
            LavaHackLoaderCoreMod.LOGGER.info("Disconnected from remote server")
        } else {
            if (reconnectionTriesCount > 5) {
                LavaHackLoaderCoreMod.LOGGER.error("Stopping reconnection to remote server, shutting down!")

                Utility.popupErrorDialog("Stopping reconnection to remote server, shutting down!", true)
            } else {
                LavaHackLoaderCoreMod.LOGGER.error("Lost connection to remote server with code: \"$code\" and reason: \"${reason ?: "no reason"}\"")
                LavaHackLoaderCoreMod.LOGGER.error("Trying to reconnect!")

                Utility.popupErrorDialog("Lost connection to remote server!", false)

                connect()

                reconnectionTriesCount++
            }
        }
    }

    override fun onError(
        ex : Exception
    ) {
        if(ex.message == "Connection refused: connect") {
            LavaHackLoaderCoreMod.LOGGER.error("Remove server is offline, shutting down!")

            Utility.popupErrorDialog("Remove server is offline, shutting down!", true)
        } else {
            LavaHackLoaderCoreMod.LOGGER.error("Received error from web client", ex)
        }
    }
}

interface IMessageProcessor {
    fun processMessage(
        message : String
    )

    fun processMessage(
        buff : ByteBuffer
    )
}

class DefaultMessageProcessor : IMessageProcessor {
    override fun processMessage(
        message : String
    ) {

    }

    override fun processMessage(
        buff : ByteBuffer
    ) {

    }
}

class DummyMessageProcessor : IMessageProcessor {
    override fun processMessage(
        message : String
    ) { }

    override fun processMessage(
        buff : ByteBuffer
    ) { }
}