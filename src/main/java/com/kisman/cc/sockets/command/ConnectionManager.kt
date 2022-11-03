package com.kisman.cc.sockets.command

import com.kisman.cc.sockets.client.SocketClient
import com.kisman.cc.sockets.data.SocketMessage
import com.kisman.cc.sockets.setupSocketClient
import com.kisman.cc.util.Globals.mc

/**
 * @author _kisman_
 * @since 14:42 of 02.11.2022
 */
object ConnectionManager {
    val client = SocketClient("161.97.78.143"/*"localhost"*/, 25563/*25564*/)

    var connected = false

    fun connect() {
        client.onMessageReceived = {
            if(it.type == SocketMessage.Type.Text) {
                CommandManager.execute(it.text!!, client)
            }
        }

        try {
            setupSocketClient(client)

            connected = true

            client.writeMessage(SocketMessage("clientstatus ${mc.session.profile.id} 1"))

            Runtime.getRuntime().addShutdownHook(Thread {
                client.writeMessage(SocketMessage("clientstatus ${mc.session.profile.id} 2"))
            })
        } catch(_ : Exception) { }
    }
}