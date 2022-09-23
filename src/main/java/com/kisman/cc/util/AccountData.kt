package com.kisman.cc.util

import com.kisman.cc.Kisman
import com.kisman.cc.sockets.client.SocketClient
import com.kisman.cc.sockets.setupSocketClient

/**
 * @author _kisman_
 * @since 19:43 of 11.09.2022
 */
object AccountData {
    var key : String? = null
    var properties : String? = null
    var processors : Int = -1

    @JvmStatic fun check() {
        val client = setupSocketClient(SocketClient("161.97.78.143", 25563))

        client.onMessageReceived = {
            if(it.text != "2" && !Kisman.runningFromIntelliJ()) {
                Kisman.unsafeCrash()
            }
        }

        client.writeMessage { text = "auth $key $properties $processors" }
    }
}