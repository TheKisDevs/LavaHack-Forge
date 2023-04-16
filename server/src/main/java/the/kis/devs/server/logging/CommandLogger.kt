package the.kis.devs.server.logging

import the.kis.devs.server.command.Command
import the.kis.devs.server.wsNameMap

/**
 * @author _kisman_
 * @since 18:31 of 12.04.2023
 */
class CommandLogger(
    name : String,
    private val command : Command
) : Logger(
    name
) {
    override fun prefix(): String = " <${wsNameMap[command.connection] ?: "NULL"}/${command.command}>"
}