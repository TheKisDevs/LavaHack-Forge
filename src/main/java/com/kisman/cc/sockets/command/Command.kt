package com.kisman.cc.sockets.command

import com.kisman.cc.sockets.client.SocketClient
import com.kisman.cc.sockets.data.SocketMessage
import java.util.*

/**
 * @author _kisman_
 * @since 14:23 of 05.07.2022
 */
abstract class Command(
    val command : String
) : ICommand {
    var client : SocketClient? = null

    fun runCommand(
        line : String,
        args : List<String>,
        client : SocketClient
    ) {
        this.client = client

        for(message in execute(line, args)) {
//            println("Answer by command \"$command\" from client \"${client.name}\" is \"${if(message.type == SocketMessage.Type.Text) message.text else message.file?.name}\"")

            if(message.type == SocketMessage.Type.Text) {
                message.text = "true ${Base64.getEncoder().encodeToString(message.text?.toByteArray())}"
//                println("Encoded answer by command \"$command\" from client \"${client.name}\" is \"${if(message.type == SocketMessage.Type.Text) message.text else message.file?.name}\"")
            }

            client.writeMessage(message)
        }
    }
}