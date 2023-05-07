package com.kisman.cc.features.module.render.charms

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.PacketEvent
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.features.module.render.charms.logoutspots.EntityLogged
import com.kisman.cc.util.copy
import com.kisman.cc.util.findName
import com.kisman.cc.util.string2int
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.network.play.server.SPacketPlayerListItem
import java.util.*

/**
 * @author _kisman_
 * @since 8:02 of 02.04.2023
 */
@ModuleInfo(
    name = "LogoutSpotsRewrite2",
    display = "Logs",
    submodule = true
)
class LogoutSpotsRewrite2 : Module() {
    override fun onEnable() {
        super.onEnable()
        Kisman.EVENT_BUS.subscribe(receive)
    }

    override fun onDisable() {
        super.onDisable()
        Kisman.EVENT_BUS.unsubscribe(receive)
    }

    private val receive = Listener<PacketEvent.Receive>(EventHook { it0 ->
        val packet = it0.packet

        if(packet is SPacketPlayerListItem) {
            for(entry in packet.entries) {
                if(entry != null && entry.profile.id != null) {
                    val name = entry.profile.name ?: findName(entry.profile.id)

                    if(name != null) {
                        when (packet.action) {
                            SPacketPlayerListItem.Action.REMOVE_PLAYER -> {
                                val player = mc.world.getPlayerEntityByUUID(entry.profile.id)

                                if (player != null) {
                                    EntityLogged(mc.world, entry.profile, player, moduleId + string2int(player.name) + Random().nextInt()).also {
                                        copy(player, it)
                                    }
                                }
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    })
}