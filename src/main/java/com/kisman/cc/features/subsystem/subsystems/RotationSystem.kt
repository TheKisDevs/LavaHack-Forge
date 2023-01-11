package com.kisman.cc.features.subsystem.subsystems

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.PacketEvent
import com.kisman.cc.features.subsystem.SubSystem
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.TimerUtils
import com.kisman.cc.util.minecraft.positionRotation
import com.kisman.cc.util.minecraft.rotation
import com.kisman.cc.util.minecraft.sendPacketNoEvent
import com.kisman.cc.util.world.RotationUtils
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.entity.Entity
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.gameevent.TickEvent

/**
 * @author _kisman_
 * @since 21:44 of 10.01.2023
 */
object RotationSystem : SubSystem(
    "Rotation System"
) {
    private var lastPacket : CPacketPlayer? = null
    private val timer = TimerUtils()

    @JvmField var takeOffDelaySetting : Setting? = null

    override fun init() {
        MinecraftForge.EVENT_BUS.register(this)
        Kisman.EVENT_BUS.subscribe(send)
    }

    override fun update(
        event : TickEvent.ClientTickEvent
    ) {
        if(lastPacket != null && takeOffDelaySetting != null && timer.passedMillis(takeOffDelaySetting!!.valLong)) {
            sendLastPacket()
        }
    }

    @EventHandler
    private val send = Listener<PacketEvent.Send>(EventHook {
        val packet = it.packet

        if(packet is CPacketPlayer/* || packet is CPacketPlayer.Position || packet is CPacketPlayer.PositionRotation || packet is CPacketPlayer.Rotation*//* && !it.cancelled*/) {
            reset()
            it.cancel()
            timer.reset()
            lastPacket = packet/* as CPacketPlayer*/
        }
    })

    @JvmStatic fun handleRotate(
        pos : BlockPos
    ) : Boolean = handleRotate(RotationUtils.getRotation(pos))

    @JvmStatic fun handleRotate(
        entity : Entity
    ) : Boolean = handleRotate(RotationUtils.getRotation(entity))

    @JvmStatic fun handleRotate(
        rotations : FloatArray
    ) : Boolean = handleRotate(rotations[0], rotations[1])

    @JvmStatic fun handleRotate(
        yaw : Float,
        pitch : Float
    ) : Boolean = if(lastPacket == null) {
        false
    } else if((mc.player == null || (mc.player.rotationYaw == yaw && mc.player.rotationPitch == pitch))) {
        false//TODO: make this check better
    } else {
        val currentYaw = lastPacket!!.yaw
        val currentPitch = lastPacket!!.yaw

        if(currentYaw - yaw == 0f || currentPitch - pitch == 0f) {
            if(!lastPacket!!.rotating && !lastPacket!!.moving /*TODO: ground check will be cool*/) {
                lastPacket = null
            } else {
                sendLastPacket()
            }
        } else {
            if(lastPacket!!.rotating) {
                lastPacket!!.yaw = yaw
                lastPacket!!.yaw = pitch
            } else if(lastPacket!!.moving) {
                lastPacket = positionRotation(
                    lastPacket!!.x,
                    lastPacket!!.y,
                    lastPacket!!.z,
                    yaw,
                    pitch,
                    lastPacket!!.onGround
                )
            } else {
                lastPacket = rotation(
                    yaw,
                    pitch,
                    lastPacket!!.onGround
                )
            }

            sendLastPacket()
        }

        true
    }

    fun reset() {
        if(lastPacket != null && mc.player != null && mc.world != null) {
            sendLastPacket()
        }
    }

    private fun sendLastPacket() {
        if(lastPacket != null && mc.player != null && mc.world != null) {
            sendPacketNoEvent(lastPacket!!)
        }

        lastPacket = null
    }
}