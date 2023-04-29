package com.kisman.cc.features.hud.modules

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.PacketEvent
import com.kisman.cc.features.hud.AverageMultiLineHudModule
import com.kisman.cc.features.hud.MultiLineElement
import com.kisman.cc.util.client.AnimateableFeature
import com.kisman.cc.util.potions
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.client.resources.I18n
import net.minecraft.network.play.server.SPacketEntityEffect
import net.minecraft.network.play.server.SPacketRemoveEntityEffect
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect
import net.minecraft.util.text.TextFormatting

/**
 * @author _kisman_
 * @since 15:58 of 29.03.2023
 */
class PotionHud2 : AverageMultiLineHudModule(
    "PotionHud",
    "oh god"
) {
    private val map1 = mutableMapOf<Potion, AnimateableFeature>()

    override fun onEnable() {
        super.onEnable()
        Kisman.EVENT_BUS.subscribe(receive)
    }

    override fun onDisable() {
        super.onDisable()
        Kisman.EVENT_BUS.unsubscribe(receive)
    }

    override fun elements(
        elements : ArrayList<MultiLineElement>
    ) {
        for(potion in potions) {
            if(!map1.contains(potion)) {
                map1[potion] = AnimateableFeature(this)
            }

            val effect = mc.player.activePotionMap[potion]

            elements.add(MultiLineElement(map1[potion]!!, format(effect ?: PotionEffect(potion, 0, 0))) { mc.player.activePotionMap.contains(potion) })
        }
    }

    private fun format(
        effect : PotionEffect
    ) = I18n.format(effect.effectName) + (if(effect.amplifier > 0) " " + effect.amplifier else "") + TextFormatting.GRAY + ": " + Potion.getPotionDurationString(effect, 1f)

    private val receive = Listener<PacketEvent.Receive>(EventHook {
        val packet = it.packet

        if(packet is SPacketEntityEffect) {
            if(mc.player.entityId == packet.entityId) {
                val potion = Potion.getPotionById(packet.effectId.toInt())!!

                map1[potion] = AnimateableFeature(this)
            }
        } else if(packet is SPacketRemoveEntityEffect) {
            if(mc.player == packet.getEntity(mc.world)) {
                map1.remove(packet.potion)
            }
        }
    })
}