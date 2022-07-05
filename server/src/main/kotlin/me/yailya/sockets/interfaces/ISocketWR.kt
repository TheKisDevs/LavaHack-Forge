/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */

package me.yailya.sockets.interfaces

import java.net.Socket
import java.net.SocketException

interface ISocketWR {
    val socket: Socket

    val connected get() = socket.isConnected &&
            !socket.isClosed &&
            !(get(socket.getInputStream(), "impl.isConnectionResetPending") as Boolean) &&
            !(get(socket.getInputStream(), "impl.isClosedOrPending") as Boolean)

    /**
     * Closes the socket
     */
    fun close() {
        if (!socket.isClosed) {
            socket.close()
        }
    }

    /**
     * Sends text to the socket
     */
    fun writeString(text: String) {
        writeBytes(text.toByteArray(Charsets.UTF_8))
    }

    /**
     * Sends bytes to the socket
     */
    fun writeBytes(bytes: ByteArray) {
        try {
            if (connected) {
                socket.getOutputStream().write(bytes)
            }
        } catch (ex: SocketException) {
            close()
        }
    }

    /**
     * Reads text from the socket
     */
    fun readString(): String? {
        return readBytes()?.toString(Charsets.UTF_8)
    }

    /**
     * Reads bytes from the socket
     */
    fun readBytes(): ByteArray? {
        try {
            if (connected) {
                val buffer = ByteArray(1024)
                val bufferLength = socket.getInputStream().read(buffer)

                return buffer.copyOf(bufferLength)
            }
        } catch (ex: SocketException) {
            close()
        }

        return null
    }

    private fun get(instance: Any, path: String): Any {
        var currentInstance = instance
        val currentClazz = { currentInstance.javaClass }

        path.split(".").forEach { pathPart ->
            when {
                currentClazz().fields.any { it.name == pathPart } -> {
                    currentInstance = currentClazz()
                        .getField(pathPart)
                        .apply { isAccessible = true }
                        .get(currentInstance)
                }
                currentClazz().declaredFields.any { it.name == pathPart } -> {
                    currentInstance = currentClazz()
                        .getDeclaredField(pathPart)
                        .apply { isAccessible = true }
                        .get(currentInstance)
                }
                currentClazz().methods.any { it.name == pathPart } -> {
                    currentInstance = currentClazz()
                        .getMethod(pathPart)
                        .apply { isAccessible = true }
                        .invoke(currentInstance)
                }
                currentClazz().declaredMethods.any { it.name == pathPart } -> {
                    currentInstance = currentClazz()
                        .getDeclaredMethod(pathPart)
                        .apply { isAccessible = true }
                        .invoke(currentInstance)
                }
            }
        }

        return currentInstance
    }
}