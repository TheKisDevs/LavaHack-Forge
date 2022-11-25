package the.kis.devs.discordbot.feature.command

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.utils.data.SerializableData

/**
 * @author _kisman_
 * @since 21:53 of 25.11.2022
 */
abstract class SlashCommand(
    command : String,
    description : String,
    vararg options : OptionData
) : Command<SlashCommandInteractionEvent>(
    command,
    description,
    *options
) {
    override fun run(
        message : Array<out String>,
        event : SlashCommandInteractionEvent
    ) {}

    @Throws(Exception::class)
    abstract fun run(
        event : SlashCommandInteractionEvent
    )
}