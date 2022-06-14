package com.kisman.cc.features.module.misc

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.PacketEvent
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.network.play.client.CPacketConfirmTeleport

/**
 * @author _kisman_
 * @since 20:02 of 13.06.2022
 */
class PortalsModifier : Module(
    "PortalsModifier",
    "Extra nether portals features.",
    Category.MISC
) {
    private val allowGuis = register(Setting("Allow Guis", this, true))
    private val godMode = register(Setting("God Mode", this, false))

    override fun onEnable() {
        super.onEnable()
        Kisman.EVENT_BUS.subscribe(send)
    }

    override fun onDisable() {
        super.onDisable()
        Kisman.EVENT_BUS.unsubscribe(send)
    }

    override fun update() {
        if(mc.player == null || mc.world == null) {
            return
        }

        doAllowGuis()
    }

    private fun doAllowGuis() {
        if(allowGuis.valBoolean) {
            mc.player.inPortal = false
        }
    }

    private val send = Listener<PacketEvent.Send>(EventHook {
        if(it.packet is CPacketConfirmTeleport && godMode.valBoolean) {
            it.cancel()
        }
    })
}