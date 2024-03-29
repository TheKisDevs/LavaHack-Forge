/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */

package me.yailya.sockets.interfaces

import the.kis.devs.server.util.Constants
import the.kis.devs.server.data.SocketMessage
import java.net.Socket
import java.nio.ByteBuffer

@Suppress("unused")
interface ISocketRW {
    val socket: Socket

    val connected
        get() = socket.isConnected && !socket.isClosed

    /**
     * Closes the socket
     */
    fun close() {
        if (!socket.isClosed) {
            socket.close()
        }
    }

    /**
     * Sends message to the socket
     */
    fun writeMessage(message: SocketMessage) {
        writeBytes(message.byteArray)
    }

    /**
     * Sends messages to the socket
     */
    fun writeMessages(vararg messages: SocketMessage) {
        messages.forEach { writeMessage(it) }
    }

    /**
     * Sends message to the socket
     */
    fun writeMessage(block: SocketMessage.() -> Unit) {
        writeMessage(SocketMessage(ByteArray(0)).apply(block))
    }

    /**
     * Sends messages to the socket
     */
    fun writeMessages(vararg messages: SocketMessage.() -> Unit) {
        messages.forEach { writeMessage(it) }
    }

    /**
     * Sends bytes to the socket
     */
    fun writeBytes(byteArray: ByteArray) {
        try {
            if (connected) {
                socket.getOutputStream().write(ByteBuffer.allocate(Int.SIZE_BYTES).putInt(byteArray.size).array())
                socket.getOutputStream().flush()
                socket.getOutputStream().write(byteArray)
                socket.getOutputStream().flush()
            }
        } catch (ex: Exception) {
            if (ex.message != "Connection reset") {
                ex.printStackTrace()
            }

            close()
        }
    }

    /**
     * Reads message from the socket
     */
    fun readMessage(): SocketMessage? {
        return SocketMessage(readBytes() ?: return null)
    }


    /**
     * Reads bytes from the socket
     */
    fun readBytes(): ByteArray? {
        try {
            if (connected) {
                val sizeBuffer = ByteArray(4)

                if (socket.getInputStream().read(sizeBuffer) != 4) {
                    return null
                }

                var totalBufferSize = ByteBuffer.wrap(sizeBuffer).int

                if (totalBufferSize > Constants.MAX_PACKET_SIZE) {
                    val buffers = mutableListOf<ByteArray>()
                    var buffer = ByteArray(totalBufferSize)
                    var bufferSize: Int

                    while (socket.getInputStream().read(buffer).also { bufferSize = it } != -1) {
                        totalBufferSize -= bufferSize
                        buffers.add(buffer.copyOf(bufferSize))
                        buffer = ByteArray(bufferSize)

                        if (totalBufferSize == 0) {
                            break
                        }
                    }

                    return buffers.flatMap { it.asIterable() }.toByteArray()
                } else {
                    val buffer = ByteArray(totalBufferSize)
                    socket.getInputStream().read(buffer)

                    return buffer
                }
            }
        } catch (ex: Exception) {
            if (ex.message != "Connection reset") {
                ex.printStackTrace()
            }

            close()
        }

        return null
    }

    private fun get(instance: Any, path: String): Any {
        var currentInstance = instance
        val currentClazz = { currentInstance.javaClass }

        path.split(".").forEach { pathPart ->
            when {
                currentClazz().declaredFields.any { it.name == pathPart } -> {
                    currentInstance = currentClazz()
                        .getDeclaredField(pathPart)
                        .apply { isAccessible = true }
                        .get(currentInstance)
                }
                currentClazz().declaredMethods.any { it.name == pathPart } -> {
                    currentInstance = currentClazz()
                        .getDeclaredMethod(pathPart)
                        .apply { isAccessible = true }
                        .invoke(currentInstance)
                }
                currentClazz().fields.any { it.name == pathPart } -> {
                    currentInstance = currentClazz()
                        .getField(pathPart)
                        .apply { isAccessible = true }
                        .get(currentInstance)
                }
                currentClazz().methods.any { it.name == pathPart } -> {
                    currentInstance = currentClazz()
                        .getMethod(pathPart)
                        .apply { isAccessible = true }
                        .invoke(currentInstance)
                }
            }
        }

        return currentInstance
    }
}
