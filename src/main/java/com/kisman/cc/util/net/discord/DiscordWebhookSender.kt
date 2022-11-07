package com.kisman.cc.util.net.discord

import com.kisman.cc.util.Colour
import java.io.IOException

class DiscordWebhookSender {
    companion object {
        fun send(webhook: String, color: Colour, title: String) {
            val sender = DiscordWebhook(webhook)
            sender.addEmbed(DiscordWebhook.EmbedObject().setColor(color.color).setTitle(title))

            try{
                sender.execute()
            } catch (e: IOException) {}
        }
    }
}