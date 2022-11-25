package the.kis.devs.discordbot.feature.command

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.utils.data.SerializableData

/**
 * @author _kisman_
 * @since 21:53 of 25.11.2022
 */
abstract class NormalCommand(
    command : String,
    description : String,
    vararg options : OptionData
) : Command<MessageReceivedEvent>(
    command,
    description,
    *options
) {
    @Throws(Exception::class)
    override fun run(
        message : Array<out String>,
        event : MessageReceivedEvent
    ) {}

    override fun registerSlashCommand(
        guild : Guild
    ) { }
}