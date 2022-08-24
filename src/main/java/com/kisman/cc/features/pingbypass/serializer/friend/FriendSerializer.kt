package com.kisman.cc.features.pingbypass.serializer.friend

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.client.friend.FriendEvent
import com.kisman.cc.features.module.client.PingBypass
import com.kisman.cc.features.pingbypass.serializer.Serializer
import com.kisman.cc.pingbypass.server.PingBypassServer
import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.manager.friend.FriendManager
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.network.play.client.CPacketChatMessage
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent

/**
 * @author _kisman_
 * @since 23:05 of 19.08.2022
 */
class FriendSerializer : Serializer<FriendEvent> {
    private val changed = HashSet<FriendEvent>()

    private val friendEvent = Listener<FriendEvent>(EventHook {
        if(PingBypassServer.connected())
        changed.add(it)
    })

    init {
        Kisman.EVENT_BUS.subscribe(friendEvent)
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onClientTick(event : TickEvent.ClientTickEvent) {
        if(mc.player != null && mc.player.connection != null && changed.isNotEmpty()) {
            val next = pollFriend()

            if(next != null) {
                serializeAndSend(next)
            }
        }
    }

    @SubscribeEvent
    fun onDisconnect(event : FMLNetworkEvent.ClientDisconnectionFromServerEvent) {
        clear()
    }

    fun clear() {
        synchronized(changed) {
            changed.clear()

            for(friend in  FriendManager.instance.friends) {
                changed.add(FriendEvent(friend, FriendEvent.Type.Add))
            }
        }
    }

    private fun pollFriend() : FriendEvent? {
        if(changed.isNotEmpty()) {
            val next = changed.iterator().next()
            changed.remove(next)
            return next
        }

        return null
    }

    override fun serializeAndSend(
        event : FriendEvent
    ) {
        if(PingBypass.isToggled) {
            mc.player.connection.sendPacket(CPacketChatMessage("@ServerFriend ${if (event.type == FriendEvent.Type.Add) "add" else "remove"} ${event.name}"))
        }
    }
}