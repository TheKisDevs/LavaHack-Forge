package the.kis.devs.discordbot.feature.command.commands

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.utils.FileUpload
import the.kis.devs.discordbot.feature.command.NormalCommand

/**
 * @author _kisman_
 * @since 20:11 of 12.11.2022
 */
class AvatarCommand : NormalCommand(
    "avatar",
    "shows members avatar"
) {
    private fun sendAvatar(
        member : Member,
        event : MessageReceivedEvent
    ) {
        val `is` = member.effectiveAvatar.download()

        while(!`is`.isDone) { }

        val toSend = StringBuilder("<@${member.id}>'s avatar:")

        channelsManager.send(toSend.toString(), event.channel.id, FileUpload.fromData(`is`.get(), "${member.id}.png"))
    }

    override fun run(
        message : Array<out String>,
        event : MessageReceivedEvent
    ) {
        if(message.size == 1) {
            sendAvatar(event.member!!, event)
        } else if(message.isNotEmpty()) {
            event.message.mentions.members.forEach { sendAvatar(it, event) }
        }
    }
}