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
        "Chat features",
        Category.MISC
) {
    val animation = register(Setting("Animation", this, false))
    private val suffix = register(Setting("Suffix", this, false))
    private val antiSpamBypass = register(Setting("Anti Spam Bypass", this, false))
    private val autoGlobal = register(Setting("Auto Global", this, false))
    private val greenText = register(Setting("Green Text", this, false))
    val customY = register(Setting("Custom Y", this, false))
    val customYVal = register(Setting("Custom Y Value", this, 50.0, 0.0, 100.0, true).setVisible { customY.valBoolean })
    val ttf = register(Setting("TTF", this, false))

    @SubscribeEvent fun onChat(event: ClientChatEvent) {
        var firstchar = arrayOf('/', '.', ',', ';', ':', '-', '+', Kisman.instance.commandManager.cmdPrefixStr)
        if (!firstchar.contains(event.message[0])) {
            if(greenText.valBoolean) {
                event.message = "> ${event.message}"
            }

            if(autoGlobal.valBoolean) {
                event.message = "!${event.message}"
            }

            if(suffix.valBoolean) {
                event.message = "${event.message} | ${Kisman.getName()} own you and all"
            }
            if(antiSpamBypass.valBoolean) {
                event.message = "${event.message} | ${Random.nextInt()}"
            }
        }
    }
}