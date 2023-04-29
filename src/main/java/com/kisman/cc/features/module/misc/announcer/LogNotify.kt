package com.kisman.cc.features.module.misc.announcer

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.PacketEvent
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.chat.cubic.ChatUtility
import com.kisman.cc.util.findName
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.network.play.server.SPacketPlayerListItem
import java.util.*

/**
 * @author _kisman_
 * @since 9:55 of 24.04.2023
 */
@ModuleInfo(
    name = "LogNotify",
    display = "Logs",
    submodule = true
)
class LogNotify : Module() {
    private val joins = register(Setting("Joins", this, true))
    private val leaves = register(Setting("Leaves", this, true))

    private val cache = mutableSetOf<UUID>()

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
                    val player = mc.world.getPlayerEntityByUUID(entry.profile.id)

                    if(name != null && player != null) {
                        when (packet.action) {
                            SPacketPlayerListItem.Action.ADD_PLAYER -> {
                                if(joins.valBoolean && cache.contains(entry.profile.id)) {
                                    ChatUtility.message().printClientModuleMessage("$name got back at ${player.position.x} ${player.position.y} ${player.position.z}")
                                    cache.remove(entry.profile.id)
                                }
                            }

                            SPacketPlayerListItem.Action.REMOVE_PLAYER -> {
                                if(leaves.valBoolean) {
                                    ChatUtility.message().printClientModuleMessage("$name logged out at ${player.position.x} ${player.position.y} ${player.position.z}")
                                    cache.add(entry.profile.id)
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