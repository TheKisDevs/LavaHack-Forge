package the.kis.devs.server.permission

import me.yailya.sockets.server.SocketServerConnection

/**
 * @author _kisman_
 * @since 22:03 of 05.07.2022
 */
interface IPermission {
    fun check(connection : SocketServerConnection) : Boolean
}