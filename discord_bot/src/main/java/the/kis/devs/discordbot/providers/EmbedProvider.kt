package the.kis.devs.discordbot.providers

import net.dv8tion.jda.api.entities.EmbedType
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.MessageEmbed.Field
import java.time.OffsetDateTime

/**
 * @author _kisman_
 * @since 22:20 of 12.11.2022
 */
class EmbedProvider {
    companion object {
        @JvmStatic val githubURL = "https://github.com/TheKisDevs/LavaHack"
        @JvmStatic val iconURL = "https://media.discordapp.net/attachments/869676044314112041/1041072269084074085/kccnewpixil-frame-0_2.png?width=606&height=606"

        @JvmStatic fun build(
            title : String,
            color : Int,
            imageURL : String?,
            vararg fields : Field
        ) : MessageEmbed = MessageEmbed(
            null,//"https://github.com/TheKisDevs/LavaHack",
            title,
            null,
            EmbedType.AUTO_MODERATION,
            OffsetDateTime.now(),
            color,
            null,//Thumbnail("thumbnail url", "proxy url", 100, 100),
            null,//Provider("provider name", "provider url"),
            MessageEmbed.AuthorInfo("\\_kisman_ | TheKisDevs", githubURL, iconURL, iconURL),
            null,//VideoInfo("video url", 100, 100),
            null,//MessageEmbed.Footer("footer text", "footer url", "proxy url"),
            MessageEmbed.ImageInfo(imageURL, imageURL, 100, 100),
            listOf(
                MessageEmbed.Field("field1 name", "field1 value", true, false),
                MessageEmbed.Field("field2 name", "field2 value", true, true),
                MessageEmbed.Field("field3 name", "field3 value", false, true),
                MessageEmbed.Field("field4 name", "field4 value", false, false)
            )
        )
    }
}