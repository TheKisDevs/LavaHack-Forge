package the.kis.devs.server.websocket

import the.kis.devs.server.websockets.WebSocket
import the.kis.devs.server.websockets.handshake.ClientHandshake

/**
 * @author _kisman_
 * @since 14:14 of 05.01.2023
 */
interface IMessageProcessor {
    fun onOpen(
        conn : WebSocket?,
        handshake : ClientHandshake?
    )

    fun onClose(
        conn : WebSocket?,
        code : Int,
        reason : String?,
        remote : Boolean
    )

    fun onMessage(
        conn : WebSocket?,
        message : String
    )
}