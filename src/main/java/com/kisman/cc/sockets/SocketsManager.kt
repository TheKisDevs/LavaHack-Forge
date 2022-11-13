package com.kisman.cc.sockets

import com.kisman.cc.sockets.client.SocketClient
import com.kisman.cc.util.AccountData

/**
 * @author _kisman_
 * @since 21:52 of 13.09.2022
 */

fun reportIssue(
    message : String
) {
    val client = setupSocketClient(SocketClient("161.97.78.143", 25563))
    client.writeMessage { text = "sendmessage Received new message: \"$message\", from \"${AccountData.key}\"" }
    client.close()
}

fun setupSocketClient(
    client : SocketClient
) : SocketClient {
    client.connect()
    client.writeMessage { text = "LavaHack-Client" }

    Runtime.getRuntime().addShutdownHook(Thread {
        if(client.connected) {
            client.close()
        }
    })

    return client
}