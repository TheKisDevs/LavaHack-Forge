package the.kis.devs.client

import me.yailya.sockets.client.SocketClient
import me.yailya.sockets.data.SocketMessage
import the.kis.devs.server.ADDRESS
import the.kis.devs.server.PORT
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * @author _kisman_
 * @since 16:31 of 06.07.2022
 */
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