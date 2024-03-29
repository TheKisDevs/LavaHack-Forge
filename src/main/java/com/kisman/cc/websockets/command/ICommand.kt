package com.kisman.cc.websockets.command

import com.kisman.cc.websockets.data.SocketMessage

/**
 * @author _kisman_
 * @since 14:21 of 05.07.2022
 */
interface ICommand {
    fun execute(
        line : String,
        args : List<String>
    ) : List<SocketMessage>
}