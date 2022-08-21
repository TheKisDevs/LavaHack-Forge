package com.kisman.cc.pingbypass.server.nethandler

import com.kisman.cc.mixin.mixins.accessor.AccessorC00Handshake
import com.kisman.cc.pingbypass.server.PingBypassServer
import com.kisman.cc.pingbypass.server.managers.PacketManager
import com.kisman.cc.pingbypass.server.protocol.ProtocolFactoryImpl
import com.kisman.cc.pingbypass.utils.disconnectFromMC
import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.getPing
import com.kisman.cc.util.world.Locks
import io.netty.util.concurrent.GenericFutureListener
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.multiplayer.GuiConnecting
import net.minecraft.client.network.NetHandlerPlayClient
import net.minecraft.network.NetworkManager
import net.minecraft.network.Packet
import net.minecraft.network.handshake.client.C00Handshake
import net.minecraft.network.play.client.*
import net.minecraft.network.play.client.CPacketEntityAction.Action.*
import net.minecraft.network.play.server.SPacketDisconnect
import net.minecraft.network.play.server.SPacketKeepAlive
import net.minecraft.util.text.TextComponentString

/**
 * @author _kisman_
 * @since 15:50 of 20.08.2022
 */
class PingBypassNetHandler(
    manager : NetworkManager
) : BaseNetHandler(
    manager,
    30000
),
    IPingBypassNetHandler
{
    private val factory = ProtocolFactoryImpl()

    override fun handle(
        packet : Packet
        <*>) {
        mc.addScheduledTask {
            if(mc.connection != null) {
                send(
                    packet,
                    mc.connection!!
                )
            }
        }
    }

    private fun send(
        packet : Packet<*>,
        client : NetHandlerPlayClient
    ) {
        PacketManager.authorize(packet)
        client.sendPacket(packet)
    }

    companion object {
        fun onLogin(
            manager : NetworkManager,
            handshake : C00Handshake?
        ) {
            mc.addScheduledTask(Locks.wrap(Locks.PINGBYPASS_PACKET_LOCK) {
                if (PingBypassServer.connected) {
                    val reason = TextComponentString("This PingBypass is currently in use1")

                    manager.sendPacket(
                        SPacketDisconnect(
                            reason
                        ),
                        GenericFutureListener {
                            manager.closeChannel(
                                reason
                            )
                        }
                    )
                }

                PingBypassServer.connected = true
                PingBypassServer.manager = manager

                if(handshake != null && (handshake as AccessorC00Handshake).ip() != null) {
                    try {
                        //TODO: DISCONNECT_SERVICE.setAllow(true)
                        disconnectFromMC("Joining other server...")
                    } finally {
                        //TODO: DISCONNECT_SERVICE.setAllow(false)
                    }

                    val ipRaw = (handshake as AccessorC00Handshake).ip()

                    val ip = if(ipRaw.contains("\u0000FML\u0000")) {
                        ipRaw.split("\u0000")[0]
                    } else {
                        ipRaw
                    }

                    val port = (handshake as AccessorC00Handshake).port()

                    manager.netHandler = PingBypassNetHandler(manager)
                    //if we will make AutoConfig module, i will add this packet :skull:
                    //TODO: manager.sendPacket(S2CActualServerPacket(ip))

                    mc.displayGuiScreen(GuiConnecting(
                        mc.currentScreen ?: GuiMainMenu(),
                        mc,
                        ip,
                        (handshake as AccessorC00Handshake).port()
                    ))
                } else if(mc.world == null || mc.player == null) {
                    manager.netHandler = WaitingForJoinHandler(manager)
                    //TODO: PingBypassWorldSender.sendWorld(manager)
                } else {
                    manager.netHandler = PingBypassNetHandler(manager)

                    val data = mc.currentServerData

                    //if we will make AutoConfig module, i will add this packet :skull:
                    //if(data?.serverIP != null) {
                        //TODO: manager.sendPacket(S2CActualServerPacket(data.serverID))
                    //}

                    //TODO: WorldSender.sendWorld(world, player, manager)
                }
            })
        }
    }

    override fun processClickWindow(
        packet : CPacketClickWindow
    ) {
        super.processClickWindow(packet)
    }

    override fun processCloseWindow(
        packet : CPacketCloseWindow
    ) {
        handle(packet)
        mc.addScheduledTask {
            mc.player.closeScreen()
        }
    }

    override fun processCustomPayload(
        packet : CPacketCustomPayload
    ) {
        if("PingBypass" == packet.channelName) {
            factory.handle(
                packet.bufferData,
                manager
            )
        } else {
            handle(packet)
        }
    }

    override fun processKeepAlive(
        packet : CPacketKeepAlive
    ) {
        timer.reset()
        manager.sendPacket(SPacketKeepAlive(getPing().toLong()))
    }

    override fun processPlayer(
        packet : CPacketPlayer
    ) {
        timer.reset()
        mc.addScheduledTask {
            if(mc.player != null) {
                val actualPos = PingBypassServer.actualPos

                if(
                    actualPos == null
                    || !actualPos.valid(packet)
                ) {
                    handle(packet)
                    return@addScheduledTask
                }

                //TODO: we need some position/rotation manager
                // will add the PbRotations module to solve this for now
                //From earthhack ^^

                val x = packet.getX(mc.player.posX)
                val y = packet.getX(mc.player.posY)
                val z = packet.getX(mc.player.posZ)
                val yaw = packet.getYaw(mc.player.rotationYaw)
                val pitch = packet.getPitch(mc.player.rotationPitch)
                val ground = packet.isOnGround
            }
        }
    }

    override fun processPlayerDigging(
        packet : CPacketPlayerDigging
    ) {
        super.processPlayerDigging(packet)
    }

    override fun processEntityAction(
        packet : CPacketEntityAction
    ) {
        mc.addScheduledTask {
            if(mc.player != null) {
                when(packet.action) {
                    START_SNEAKING -> mc.player.isSneaking = true
                    STOP_SNEAKING -> mc.player.isSneaking = false
                    START_SPRINTING -> mc.player.isSprinting = true
                    STOP_SPRINTING -> mc.player.isSprinting = true
                    else -> {}
                }
            }
        }
        handle(packet)
    }

    override fun processInput(
        packet : CPacketInput
    ) {
        mc.addScheduledTask {
            //TODO: PACKET_INPUT.onInput(packet)
        }
    }

    override fun processHeldItemChange(
        packet : CPacketHeldItemChange
    ) {
        handle(packet)
        mc.addScheduledTask {
            if(mc.player != null) {
                Locks.acquire(Locks.PLACE_SWITCH_LOCK) {
                    mc.player.inventory.currentItem = packet.slotId
                }
            }
        }
    }
}