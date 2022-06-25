package com.kisman.cc.features.command

/**
 * @author _kisman_
 * @since 18:18 of 22.06.2022
 */
interface ICommand {
    fun getCommand() : String
    fun runCommand(s: String, args: Array<String>)
}