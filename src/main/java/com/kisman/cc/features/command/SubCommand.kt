package com.kisman.cc.features.command

import com.kisman.cc.util.chat.ChatHandler

/**
 * @author _kisman_
 * @since 18:08 of 22.06.2022
 */
abstract class SubCommand(
    private val command : String,
    val instance : Command
) : ICommand, ChatHandler() {
    override fun getCommand(): String {
        return command
    }
}