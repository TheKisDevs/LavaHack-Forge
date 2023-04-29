package com.kisman.cc.features.hud.modules

import com.kisman.cc.features.hud.AverageMultiLineHudModule
import com.kisman.cc.features.hud.MultiLineElement
import com.kisman.cc.features.subsystem.subsystems.EnemyManager
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.SettingsList
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.client.AnimateableFeature
import com.kisman.cc.util.client.interfaces.IFakeEntity
import com.kisman.cc.util.client.providers.range
import com.kisman.cc.util.manager.friend.FriendManager
import com.kisman.cc.util.math.sqrt2
import net.minecraft.util.text.TextFormatting
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author _kisman_
 * @since 12:34 of 20.04.2023
 */
class TextRadar2 : AverageMultiLineHudModule(
    "TextRadar",
    "Shows list of nearest players"
) {
    private val range = range(this)
    private val highlights = register(
        register(SettingGroup(Setting("Highlights", this))).add(SettingsList(
            "friends", Setting("Highlights Friends", this, false).setTitle("Friends"),
            "enemies", Setting("Highlights Enemies", this,false).setTitle("Enemies")
        ))
    )

    private val cache = mutableMapOf<UUID, Pair<AnimateableFeature, String>>()

    override fun elements(
        elements : ArrayList<MultiLineElement>
    ) {
        val current = mutableSetOf<UUID>()

        for(player in mc.world.playerEntities) {
            if(player !is IFakeEntity && player != mc.player) {
                val distance = sqrt2(mc.player.getDistanceSq(player)).toInt()
                val text = "${(player.health + player.absorptionAmount).toInt()} " + (if (FriendManager.instance.isFriend(
                        player
                    ) && highlights["friends"].valBoolean
                ) TextFormatting.AQUA else if (EnemyManager.enemy(player) && highlights["enemies"].valBoolean) TextFormatting.RED else "") + " ${player.name + TextFormatting.RESET} $distance"
                val feature = cache[player.gameProfile.id]?.first ?: AnimateableFeature(this)
                val element = MultiLineElement(
                    feature,
                    text
                ) { !range["state"].valBoolean || distance <= range["value"].valInt }

                elements.add(element)
                cache[player.gameProfile.id] = Pair(feature, text)
                current.add(player.gameProfile.id)
            }
        }

        for(entry in cache) {
            val uuid = entry.key
            val pair = entry.value
            val feature = pair.first
            val text = pair.second
            val element = MultiLineElement(
                feature,
                text
            ) { false }

            if(!current.contains(uuid)) {
                elements.add(element)
            }
        }
    }
}