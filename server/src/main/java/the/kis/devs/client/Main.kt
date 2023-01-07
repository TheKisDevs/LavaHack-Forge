package the.kis.devs.client

import me.yailya.sockets.client.SocketClient
import the.kis.devs.server.data.SocketMessage
import the.kis.devs.server.ADDRESS
import the.kis.devs.server.PORT
import the.kis.devs.server.websockets.client.WebSocketClient
import the.kis.devs.server.websockets.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI
import java.nio.ByteBuffer
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * @author _kisman_
 * @since 16:31 of 06.07.2022
 */

fun main() {
    val client = WebClient()

    var line : String?

    client.connect()

    while(readLine().also { line = it } != null) {
        if(line == "exit") {
            break
        } else {
            client.send(line)
        }
    }

    client.close()
}

class WebClient() : WebSocketClient(
    URI("ws://localhost:25563")
) {
    override fun onOpen(
        handshakedata : ServerHandshake?
    ) {
        println("Client started")
    }

    override fun onMessage(
        message : String?
    ) {
        println("Received message \"$message\"")
    }

    override fun onMessage(
        bytes : ByteBuffer
    ) {
        val message = SocketMessage(bytes.array())

        if(message.type == SocketMessage.Type.File) {
            println("Received file \"${message.file!!.name}\"")
        }

    }

    override fun onClose(
        code : Int,
        reason : String?,
        remote : Boolean
    ) {
        println("Client closed")
    }

    override fun onError(
        ex : Exception?
    ) {
        ex!!.printStackTrace()
    }

}

fun main1() {
    val client = SocketClient(ADDRESS!!, PORT)
    var bytes : ByteArray? = null
    client.connect()
    client.onMessageReceived = {
        if (it.type == SocketMessage.Type.Text) {
            println("Message from server: ${it.text}")
        } else {
            val file = it.file!!

            println("File from server (name: ${file.name}, description: ${file.description})")
            if(file.description != "CANT_BE_OPENED") {
//                println(file.byteArray.toString(Charsets.UTF_8))
            } else {
            }
//            Files.createFile(Paths.get("server\\files\\client\\${file.name}"))
//            File("server\\files\\client\\${file.name}").writeBytes(file.byteArray)
            if(file.name.endsWith(".jar")) {
                bytes = file.byteArray
                ZipInputStream(file.byteArray.inputStream()).use { zipStream ->
                    var zipEntry: ZipEntry?
                    while (zipStream.nextEntry.also { zipEntry = it } != null) {
                        val name = zipEntry!!.name
                        if (name.endsWith(".class")) {
                            println("Found new class \"${name.replace('/', '.').removeSuffix(".class")}\"")
                        }
                    }
                }
            }
        }
    }

    /** Sets custom socket name if `Constants.CUSTOM_SOCKET_NAMES` is true */
    client.writeMessage {
        text = "OP"
    }
    
    /*while(bytes == null) {}

    ZipInputStream(bytes?.inputStream()!!).use { zipStream ->
        var zipEntry: ZipEntry?
        while (zipStream.nextEntry.also { zipEntry = it } != null) {
            val name = zipEntry!!.name
            if (name.endsWith(" .class")) {
                println("Found new class \"${name.replace('/', '.').removeSuffix(".class")}\"")
            }
        }
    }*/

    while (client.connected) {
        if(bytes == null) {
        } else {
        }

        val line = readLine() ?: continue

        if (line.isEmpty()) {
            continue
        }

        client.writeMessage {
            text = line
        }

        if (line == "exit") {
            break
        }
    }
}