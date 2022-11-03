package com.kisman.cc.features.module.client

import com.kisman.cc.features.module.Beta
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.types.SettingEnum
import com.kisman.cc.util.collections.LimitedSortedMap
import com.kisman.cc.util.manager.friend.FriendManager
import net.minecraft.util.text.TextFormatting

/**
 * @author _kisman_
 * @since 15:17 of 03.11.2022
 */
@Beta
object FriendHighlight : Module(
    "FriendHighlight",
    "highlights your friends at tab, chat, nametags and other places",
    Category.CLIENT
) {
    private val color = SettingEnum("Color", this, TextFormatting.AQUA).register()

    private val cache = LimitedSortedMap<String, String>(50)

    fun modifyLine(
        line : String
    ) : String = if(isToggled && mc.player != null && mc.world != null) cache[line] ?: modify(line).also { cache[line] = it } else line


    private fun modify(
        line : String
    ) : String {
        var modified = line

        for(friend in FriendManager.instance.friends) {
            modified = modified.replace(friend, "${color.valEnum}$friend${TextFormatting.RESET}", true)
        }

        return modified
    }
}