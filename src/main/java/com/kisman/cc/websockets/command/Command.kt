package com.kisman.cc.websockets.command

import com.kisman.cc.websockets.data.SocketMessage
import com.kisman.cc.websockets.client
import java.util.*

/**
 * @author _kisman_
 * @since 14:23 of 05.07.2022
 */
abstract class Command(
    val command : String
) : ICommand {
    fun runCommand(
        line : String,
        args : List<String>
    ) {
        for(message in execute(line, args)) {
            if(message.type == SocketMessage.Type.Text) {
                message.text = "true ${Base64.getEncoder().encodeToString(message.text?.toByteArray())}"
            }

            client.send(message.byteArray)
        }
    }
}