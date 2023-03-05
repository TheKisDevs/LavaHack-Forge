package com.kisman.cc.features.module.misc

import com.kisman.cc.Kisman
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import net.minecraftforge.client.event.ClientChatEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.random.Random

@Suppress("HasPlatformType")
class ChatModifier : Module(
        "ChatModifier",
        "Extra chat features",
        Category.MISC
) {
    val animation = register(Setting("Animation", this, false))
    private val suffix_ = register(Setting("Suffix", this, false))
    private val antiSpamBypass = register(Setting("Anti Spam Bypass", this, false))
    private val autoGlobal = register(Setting("Auto Global", this, false))
    private val greenText = register(Setting("Green Text", this, false))
    val customY = register(Setting("Custom Y", this, false))
    val customYVal = register(Setting("Custom Y Value", this, 50.0, 0.0, 100.0, true).setVisible { customY.valBoolean })
    val customAlpha = register(Setting("Custom Alpha", this, false))
    val customAlphaVal = register(Setting("Custom Alpha Value", this, 255.0, 0.0, 255.0, true).setVisible { customAlpha.valBoolean })
    val ttf = register(Setting("TTF", this, false))

    private val chars = arrayOf('/', '.', ',', ';', ':', '-', '+')

    @SubscribeEvent fun onChat(
        event : ClientChatEvent
    ) {
        if (!chars.contains(event.message[0]) && event.message[0] != Kisman.instance.commandManager.prefixChar) {
            val prefix = StringBuilder()
            val suffix = StringBuilder()

            if(greenText.valBoolean) {
                prefix.append("> ")
            }

            if(autoGlobal.valBoolean) {
                prefix.append("! ")
            }

            if(suffix_.valBoolean) {
                suffix.append(" ${Kisman.getName()} own you and all")
            }

            if(antiSpamBypass.valBoolean) {
                suffix.append(" ${Random.nextInt()}")
            }

            event.message = "$prefix${event.message}$suffix"
        }
    }
}