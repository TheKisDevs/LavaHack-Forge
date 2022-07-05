/*
 * Copyright (c) 2022. ya-ilya
 */

package com.kisman.cc.util.sockets

import java.net.InetSocketAddress
import java.net.Socket

@Suppress("MemberVisibilityCanBePrivate")
class SocketClient(private val address: String, private val port: Int) {
    private val socket = Socket()
    private val thread = Thread()

    val connected get() = socket.isConnected && !thread.isInterrupted

    /**
     * Connects to the server
     */
    fun connect() {
        socket.connect(InetSocketAddress(address, port))
    }

    /**
     * Disconnects from the server
     */
    fun disconnect() {
        thread.interrupt()
        socket.close()
    }

    /**
     * Sends text to the server
     */
    fun write(text: String) {
        writeBytes(text.toByteArray(Charsets.UTF_8))
    }

    /**
     * Sends bytes to the server
     */
    fun writeBytes(bytes: ByteArray) {
        if (connected) {
            socket.getOutputStream().write(bytes)
        }
    }

    /**
     * Reads text from the server
     */
    fun read(): String? {
        return readBytes()?.toString(Charsets.UTF_8)
    }

    /**
     * Reads bytes from the server
     */
    fun readBytes(): ByteArray? {
        if (connected) {
            val buffer = ByteArray(1024)
            val bufferLength = socket.getInputStream().read(buffer)

            return buffer.copyOf(bufferLength)
        }

        return null
    }
}