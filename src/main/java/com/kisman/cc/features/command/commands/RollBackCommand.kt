package com.kisman.cc.features.command.commands

import com.kisman.cc.Kisman
import com.kisman.cc.features.command.Command
import com.kisman.cc.event.events.PacketEvent
import com.kisman.cc.util.entity.EntityVoid
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.network.Packet
import net.minecraft.network.play.client.CPacketConfirmTeleport
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.client.CPacketPlayerTryUseItem
import net.minecraft.network.play.client.CPacketVehicleMove
import net.minecraft.network.play.server.SPacketPlayerPosLook
import net.minecraft.util.EnumHand
import net.minecraft.util.math.Vec3d


/**
 * Thanks to FFP
 *
 * @author _kisman_
 * @since 12:01 of 04.06.2022
 */
class RollBackCommand() : Command("rollback") {
    companion object {
        var instance : RollBackCommand? = null
    }

    init {
        instance = this
    }

    private var pos : Vec3d? = null
    private var id : Int = -1

    override fun runCommand(s: String, args: Array<String>) {
        if(id == -1) {
            pos = Vec3d(0.0, 0.0, 0.0)
            Kisman.EVENT_BUS.subscribe(send)
            Kisman.EVENT_BUS.subscribe(receive)
        } else {
            var mode: Mode = Mode.SIMPLE // Type of rollback

            if (args?.size!! > 1) {
                mode = try {
                    Mode.valueOf(args[1].toLowerCase())
                } catch (e: IllegalArgumentException) {
                    error("Usage: $syntax")
                    return
                }
            }

            val toSend: Array<Packet<*>> = emptyArray()

            if (mode == Mode.SIMPLE) {
                toSend[0] = CPacketConfirmTeleport(id) // Set position server-side
                toSend[1] = CPacketPlayer.Rotation(
                    mc.player.rotationYaw,
                    mc.player.rotationPitch,
                    true
                ) // Refresh player chunk map
            } else if (mode == Mode.YEET) {
                if (mc.player.fishEntity != null) {
                    val fish = mc.player.fishEntity?.caughtEntity
                    if (fish != null && fish == mc.player.getRidingEntity()) {
                        val d0: Double = pos?.x!! - mc.player.fishEntity!!.posX
                        val d1: Double = pos?.y!! - mc.player.fishEntity!!.posY
                        val d2: Double = pos?.z!! - mc.player.fishEntity!!.posZ
                        fish.motionX += d0 * 0.1
                        fish.motionY += d1 * 0.1
                        fish.motionZ += d2 * 0.1
                    }
                }
                toSend[0] = CPacketConfirmTeleport(id)
                toSend[1] = CPacketPlayerTryUseItem(EnumHand.MAIN_HAND) // fishing rod yeet
            } else {
                val ride = mc.player.getRidingEntity()

                if(ride == null) {
                    error("You are not riding anything")
                    return
                }

                val evoid = EntityVoid(mc.world, 0)
                evoid.setPosition(ride.posX, ride.posY - 0.3, ride.posZ)
                if (mode == Mode.DOUBLE) {
                    toSend[0] = CPacketConfirmTeleport(id)
                    toSend[1] = CPacketPlayer.Rotation(mc.player.rotationYaw, mc.player.rotationPitch, true)
                } else { // Tmp rollback
                    toSend[0] = CPacketPlayer.Rotation(mc.player.rotationYaw, mc.player.rotationPitch, true)
                    toSend[1] = CPacketPlayerTryUseItem(EnumHand.OFF_HAND) // 9b player chunk map refresh
                }
                toSend[2] = CPacketVehicleMove(evoid)
            }

            /* TODO Add exceptions to packet canceler */
//            val intercept: PacketInterceptionModule =
//                FamilyFunPack.getModules().getByName("Packets interception") as PacketInterceptionModule
//            {
//                var i = 0
//                while (i < toSend.size && toSend[i] != null) {
//                    intercept.addException(EnumPacketDirection.SERVERBOUND, toSend[i])
//                    i++
//                }
//            }

            // Set player position to rollback position (client-side)
            mc.player.setPosition(pos?.x!!, pos?.y!!, pos?.z!!)

            // Send everything in a row, hope it gets computed within the same tick
            for(packet in toSend) {
                mc.player.connection.sendPacket(packet)
            }
            if (mode !== Mode.TMP) this.onDisconnect()

            complete(java.lang.String.format(
                "Rollback to (%.2f, %.2f, %.2f)",
                pos?.x,
                pos?.y,
                pos?.z
            ))
        }
    }

    private fun onDisconnect() {
        id = -1
        Kisman.EVENT_BUS.unsubscribe(send)
        Kisman.EVENT_BUS.unsubscribe(receive)
    }

    private val send = Listener<PacketEvent.Send>(EventHook {
        val packet = it.packet
        if(packet is CPacketConfirmTeleport) {
            it.cancel()
        }
    })

    private val receive = Listener<PacketEvent.Receive>(EventHook {
        val packet = it.packet
        if(packet is SPacketPlayerPosLook) {
            val flags = packet.flags

            val x: Double =
                if (flags.contains(SPacketPlayerPosLook.EnumFlags.X)) mc.player.posX + pos?.x!! else pos?.x!!
            val y: Double =
                if (flags.contains(SPacketPlayerPosLook.EnumFlags.Y)) mc.player.posY + pos?.y!! else pos?.y!!
            val z: Double =
                if (flags.contains(SPacketPlayerPosLook.EnumFlags.Z)) mc.player.posZ + pos?.z!! else pos?.z!!

            if (x != pos?.x!! || y != pos?.y!! || z != pos?.z!!) {
                pos = Vec3d(x, y, z)
                complete(
                    java.lang.String.format(
                        "Rollback set to (%.2f, %.2f, %.2f)",
                        pos?.x!!,
                        pos?.y!!,
                        pos?.z!!
                    )
                )
            }

            id = packet.teleportId
        }
    })

    override fun getDescription(): String {
        return "problems?"
    }

    override fun getSyntax(): String {
        return "rollback simple/double/tmp"
    }

    enum class Mode {
        SIMPLE, DOUBLE, TMP, YEET
    }
}