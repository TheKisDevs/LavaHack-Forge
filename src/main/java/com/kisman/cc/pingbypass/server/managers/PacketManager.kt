package com.kisman.cc.pingbypass.server.managers

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.PacketEvent
import com.kisman.cc.pingbypass.server.PingBypassServer
import com.kisman.cc.util.Globals.mc
import io.netty.util.internal.ConcurrentSet
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.network.Packet
import net.minecraft.network.play.client.*
import net.minecraft.network.play.client.CPacketPlayer.PositionRotation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

/**
 * @author _kisman_
 * @since 0:58 of 21.08.2022
 */
object PacketManager {
    private val allow = ThreadLocal.withInitial { false }

    private val authorized = ConcurrentSet<Packet<*>>()

    private val blackList = setOf<Class<out Packet<*>>>(
        // TODO: option to switch, pingbypass is now allowed to move
        CPacketPlayer::class.java,
        CPacketPlayer.Position::class.java,
        PositionRotation::class.java,
        CPacketPlayer.Rotation::class.java,
        CPacketConfirmTeleport::class.java,
        CPacketInput::class.java,
        CPacketVehicleMove::class.java,
        CPacketSteerBoat::class.java,
        CPacketClientSettings::class.java,
        CPacketClientStatus::class.java,
        CPacketPlayerAbilities::class.java,
        CPacketEntityAction::class.java,
        CPacketSeenAdvancements::class.java,
        CPacketCloseWindow::class.java
        // TODO: CPacketEntityAction?
        // TODO: Some CustomPayloads?
    )

    private val send = Listener<PacketEvent.Send>(EventHook {
        if(
            isUnAuthorized(it.packet)
            && PingBypassServer.connected
            && blackList.contains(it.packet.javaClass)
            && noThreadLocalFlag()
        ) {
            it.cancel()
        }
    })

    init {
        MinecraftForge.EVENT_BUS.register(this)
        Kisman.EVENT_BUS.subscribe(send)
    }

    @SubscribeEvent fun onClientTick(event : TickEvent.ClientTickEvent) {
        if(mc.player == null || mc.world == null) {
            authorized.clear()
        }
    }

    fun isUnAuthorized(
        packet : Packet<*>
    ) : Boolean = !authorized.remove(packet)

    fun noThreadLocalFlag() : Boolean = !allow.get()

    fun allowAllOnThisThread(
        allow : Boolean
    ) {
        this.allow.set(allow)
    }

    fun authorize(
        packet : Packet<*>
    ) {
        if(PingBypassServer.connected) {
            authorized.add(
                packet
            )
        }
    }
}