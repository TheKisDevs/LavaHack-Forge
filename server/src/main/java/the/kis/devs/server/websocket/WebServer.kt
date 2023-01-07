package the.kis.devs.server.websocket

import the.kis.devs.server.websockets.WebSocket
import the.kis.devs.server.websockets.handshake.ClientHandshake
import the.kis.devs.server.websockets.server.WebSocketServer
import java.net.InetSocketAddress

class WebServer(
    port : Int,
    private val messageProcessor : IMessageProcessor
) : WebSocketServer(
    InetSocketAddress(port)
) {
    override fun onOpen(
        conn : WebSocket?,
        handshake : ClientHandshake?
    ) {
        messageProcessor.onOpen(conn, handshake)
    }

    override fun onClose(
        conn : WebSocket?,
        code : Int,
        reason : String?,
        remote : Boolean
    ) {
        messageProcessor.onClose(conn, code, reason, remote)
    }

    override fun onMessage(
        conn : WebSocket?,
        message : String
    ) {
        messageProcessor.onMessage(conn, message)
    }

    override fun onError(
        conn : WebSocket?,
        ex : Exception?
    ) {

    }

    override fun onStart() {

    }
}