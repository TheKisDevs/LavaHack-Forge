package the.kis.devs.client

import me.yailya.sockets.client.SocketClient
import me.yailya.sockets.data.SocketMessage
import me.yailya.sockets.example.ADDRESS
import me.yailya.sockets.example.PORT
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author _kisman_
 * @since 16:31 of 06.07.2022
 */
fun main() {
    val client = SocketClient(ADDRESS, PORT)
    client.connect()
    client.onMessageReceived = {
        if (it.type == SocketMessage.Type.Text) {
            println("Message from server: ${it.text}")
        } else {
            val file = it.file!!

            println("File from server (name: ${file.name}, description: ${file.description})")
            if(file.description != "CANT_BE_OPENED") {
                println(file.byteArray.toString(Charsets.UTF_8))
            }

            Files.createFile(Paths.get("server\\files\\client${file.name}"))
            val file1 = File(Paths.get("server\\files\\client${file.name}").toUri())
            val stream = file1.outputStream()

            stream.write(file.byteArray)
            stream.close()
        }
    }

    /** Sets custom socket name if `Constants.CUSTOM_SOCKET_NAMES` is true */
    client.writeMessage {
        text = "OP"
    }

    while (client.connected) {
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