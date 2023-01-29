package com.kisman.cc.util.chat

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.client.console.ConsoleMessageEvent
import com.kisman.cc.gui.console.ConsoleGui
import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.chat.cubic.ChatUtility
import net.minecraft.util.text.TextFormatting

/**
 * @author _kisman_
 * @since 18:26 of 22.06.2022
 */
abstract class ChatHandler {
    companion object {
        val instance = Instance()
    }

    class Instance : ChatHandler()

    open fun message(
        message : String
    ) {
        if (mc.currentScreen is ConsoleGui) ConsoleMessageEvent(TextFormatting.GRAY.toString() + "[" + TextFormatting.WHITE + Kisman.getName() + TextFormatting.GRAY + "] " + message).post() else ChatUtility.message().printClientMessage(message)
    }

    open fun warning(
        message : String
    ) {
        if (mc.currentScreen is ConsoleGui) ConsoleMessageEvent(TextFormatting.GRAY.toString() + "[" + TextFormatting.GOLD + Kisman.getName() + TextFormatting.GRAY + "] " + message).post() else ChatUtility.warning().printClientMessage(message)
    }

    open fun complete(
        message : String
    ) {
        if (mc.currentScreen is ConsoleGui) ConsoleMessageEvent(TextFormatting.GRAY.toString() + "[" + TextFormatting.LIGHT_PURPLE + Kisman.getName() + TextFormatting.GRAY + "] " + message).post() else ChatUtility.complete().printClientMessage(message)
    }

    open fun error(
        message : String
    ) {
        if (mc.currentScreen is ConsoleGui) ConsoleMessageEvent(TextFormatting.GRAY.toString() + "[" + TextFormatting.RED + Kisman.getName() + TextFormatting.GRAY + "] " + message).post() else ChatUtility.error().printClientMessage(message)
    }

    open fun print(
        message : String
    ) {
        if (mc.currentScreen is ConsoleGui) ConsoleMessageEvent(message!!).post() else ChatUtility.message().printMessage(message)
    }
}