package the.kis.devs.server.emulate

import the.kis.devs.server.data.SocketMessage
import me.yailya.sockets.server.ISocketServerConnection
import java.net.Socket

/**
* @author _kisman_
* @since 19:44 of 19.11.2022
*/
class EmulateConnection : ISocketServerConnection {
    override val socket = Socket()
    override var name = "Socket-EmulatingMode"

    override fun writeMessage(
        message : SocketMessage
    ) {
//        println("Message to socket \"$name\" is \"$message\"")
    }
}