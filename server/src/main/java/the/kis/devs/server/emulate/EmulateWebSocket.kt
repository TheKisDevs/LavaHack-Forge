package the.kis.devs.server.emulate

import the.kis.devs.server.websockets.WebSocket
import the.kis.devs.server.websockets.drafts.Draft
import the.kis.devs.server.websockets.enums.Opcode
import the.kis.devs.server.websockets.enums.ReadyState
import the.kis.devs.server.websockets.framing.Framedata
import the.kis.devs.server.websockets.protocols.IProtocol
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import javax.net.ssl.SSLSession

/**
 * @author _kisman_
 * @since 14:30 of 05.01.2023
 */
class EmulateWebSocket : WebSocket {
    override fun close(
        code : Int,
        message : String?
    ) { }

    override fun close(
        code : Int
    ) { }

    override fun close () { }

    override fun closeConnection(
        code : Int,
        message : String?
    ) { }

    override fun send(
        text : String?
    ) { }

    override fun send(
        bytes : ByteBuffer?
    ) { }

    override fun send(
        bytes : ByteArray?
    ) { }

    override fun sendFrame(
        framedata : Framedata?
    ) { }

    override fun sendFrame(
        frames : MutableCollection<Framedata>?
    ) { }

    override fun sendPing() { }

    override fun sendFragmentedFrame(
        op : Opcode?,
        buffer : ByteBuffer?,
        fin : Boolean
    ) { }

    override fun hasBufferedData() : Boolean {
        return false
    }

    override fun getRemoteSocketAddress() : InetSocketAddress {
        return InetSocketAddress(25563)
    }

    override fun getLocalSocketAddress() : InetSocketAddress {
        return InetSocketAddress(25563)
    }

    override fun isOpen() : Boolean {
        return true
    }

    override fun isClosing() : Boolean {
        return false
    }

    override fun isFlushAndClose() : Boolean {
        return false
    }

    override fun isClosed() : Boolean {
        return false
    }

    override fun getDraft() : Draft {
        return Object() as Draft
    }

    override fun getReadyState() : ReadyState {
        return ReadyState.OPEN
    }

    override fun getResourceDescriptor() : String {
        return ""
    }

    override fun <T : Any?> setAttachment(
        attachment : T) {

    }

    override fun <T : Any?> getAttachment() : T {
        return Object() as T
    }

    override fun hasSSLSupport() : Boolean {
        return false
    }

    override fun getSSLSession() : SSLSession {
        return Object() as SSLSession
    }

    override fun getProtocol() : IProtocol {
        return Object() as IProtocol
    }
}