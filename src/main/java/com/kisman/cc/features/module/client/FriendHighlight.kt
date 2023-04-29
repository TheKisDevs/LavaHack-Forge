package com.kisman.cc.features.module.client

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.settings.types.SettingArray
import com.kisman.cc.util.client.collections.LimitedSortedMap
import com.kisman.cc.util.manager.friend.FriendManager
import com.kisman.cc.util.minecraft.*
import net.minecraft.util.text.TextFormatting

/**
 * @author _kisman_
 * @since 15:17 of 03.11.2022
 */
@ModuleInfo(
    name = "FriendHighlight",
    desc = "Highlights your friends at tab, chat, nametags and other places",
    category = Category.CLIENT,
    beta = true
)
object FriendHighlight : Module() {
    private val COLOR_FORMATTER = Formatter(TextFormatting.AQUA, TextFormatting.AQUA.friendlyName, FormatterType.Color)

    private val color = SettingArray("Color", this, COLOR_FORMATTER, getColorFormatters()).register()
    private val style = SettingArray("Style", this, DEFAULT_STYLE_FORMATTER, getStyleFormatters()).register()

    private val cache = LimitedSortedMap<String, String>(50)

    fun modifyLine(
        line : String
    ) : String = if(isToggled && mc.player != null && mc.world != null) cache[line] ?: modify(line).also { cache[line] = it } else line

    private fun modify(
        line : String
    ) : String {
        var modified = line

        for(friend in FriendManager.instance.friends) {
            modified = modified.replace(friend, "${style.valElement.original}${color.valElement.original}$friend${TextFormatting.RESET}", true)
        }

        return modified
    }
}