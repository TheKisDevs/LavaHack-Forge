package the.kis.devs.discordbot.feature.command.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.EmbedType
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.MessageEmbed.Provider
import net.dv8tion.jda.api.entities.MessageEmbed.Thumbnail
import net.dv8tion.jda.api.entities.MessageEmbed.VideoInfo
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import the.kis.devs.discordbot.feature.command.Command
import the.kis.devs.discordbot.feature.command.NormalCommand
import java.time.OffsetDateTime

/**
 * @author _kisman_
 * @since 21:31 of 12.11.2022
 */
class EmbedTestCommand : NormalCommand(
    "embedtest",
    "tests embeds"
) {
    override fun run(
        message : Array<out String>,
        event : MessageReceivedEvent
    ) {
        val message = MessageEmbed(
            null,//"https://github.com/TheKisDevs/LavaHack",
            "title",
            "desc",
            EmbedType.AUTO_MODERATION,
            OffsetDateTime.now(),
            -1,
            null,//Thumbnail("thumbnail url", "proxy url", 100, 100),
            null,//Provider("provider name", "provider url"),
            null,//MessageEmbed.AuthorInfo("author name", "author url", "author icon url", "proxy icon url"),
            null,//VideoInfo("video url", 100, 100),
            null,//MessageEmbed.Footer("footer text", "footer url", "proxy url"),
            null,///MessageEmbed.ImageInfo("image url", "proxy url", 100, 100),
            listOf(
                MessageEmbed.Field("field1 name", "field1 value", true, false),
                MessageEmbed.Field("field2 name", "field2 value", true, true),
                MessageEmbed.Field("field3 name", "field3 value", false, true),
                MessageEmbed.Field("field4 name", "field4 value", false, false)
            )
        )

        channelsManager.send(event.channel.id, message)
    }

}