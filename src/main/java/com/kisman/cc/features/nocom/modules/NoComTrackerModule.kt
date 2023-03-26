package com.kisman.cc.features.nocom.modules

import com.kisman.cc.event.events.PacketEvent
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.nocom.modules.tracker.TrackedPlayer
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.util.chat.cubic.AbstractChatMessage
import com.kisman.cc.util.chat.cubic.ChatUtility
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.client.gui.GuiDownloadTerrain
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.network.play.server.SPacketBlockChange
import net.minecraft.network.play.server.SPacketRespawn
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 12:26 of 28.08.2022
 */
@Suppress("HasPlatformType")
/*@Subscribes(
    mode = SubscribeMode.ALPINE
)*/
class NoComTrackerModule : Module(
    "Tracker",
    "tacks selected player.",
    null
) {
    val x = register(Setting("X", this, 0.0, -1000.0, 1000.0, true))
    val z = register(Setting("Z", this, 0.0, -1000.0, 1000.0, true))

    val timeOut = register(Setting("Time Out", this, 2500.0, 10.0, 10000.0, NumberType.TIME))

    val debug = register(Setting("Debug", this, false))
    val debugErrors = register(Setting("Debug Errors", this, true))
    val notify = register(Setting("Notify", this, true))

    val renderDistance = register(Setting("Render Distance", this, 4.0, 1.0, 8.0, true))

    val autoSpiral = register(Setting("Auto Spiral", this, true))
    private val autoSpiralGroup = register(SettingGroup(Setting("Auto Spiral", this)))
    val spiralTrigger = register(autoSpiralGroup.add(Setting("Spiral Trigger", this, 3.0, 2.0, 50.0, true).setTitle("Trigger")))
    val spiralPPT = register(autoSpiralGroup.add(Setting("Spiral PPT", this, 4.0, 1.0, 15.0, true).setTitle("PPT")))
    val spiralChunkStep = register(autoSpiralGroup.add(Setting("Spiral Chunk Step", this, 2.0, 1.0, 15.0, true).setTitle("Chunk Step")))
    private val spiralAutoDisable = register(autoSpiralGroup.add(Setting("Spiral Auto Disable", this, true).setTitle("Auto Off")))
    private val spiralRange = register(autoSpiralGroup.add(Setting("Spiral Range", this, 1000.0, 100.0, 10000.0, true).setTitle("Range")))

    private var player : TrackedPlayer? = null

    private var beatPos : BlockPos? = null
    private var isBeating = false

    companion object {
        var instance : NoComTrackerModule? = null
    }

    init {
        instance = this
    }

    override fun onEnable() {
        super.onEnable()

        if(mc.player == null || mc.world == null) {
            return
        }

        player = TrackedPlayer(
            x.valInt,
            z.valInt
        )
    }

    override fun onDisable() {
        super.onDisable()

        if(mc.player != null && mc.world != null && player != null) {
            ChatUtility.complete().printClientModuleMessage(player!!.getReport())
        }
    }

    override fun update() {
        if(mc.player == null || mc.world == null || mc.currentScreen is GuiDownloadTerrain) {
            return
        }

        if(player == null) {
            toggle()
        }

        if(player!!.isSpiraling) {
            if(!player!!.processSpiral(spiralRange.valInt) && spiralAutoDisable.valBoolean) {
                if(debugErrors.valBoolean) {
                    ChatUtility.error().printClientModuleMessage("Spiral scan failed... disabling module rip")
                }

                toggle()
            }

            return
        }

        if(player!!.requestChunk()) {
            isBeating = true
        }

        if(isBeating) {
            beatPos = mc.player.position.down(10)

            if(debug.valBoolean) {
                ChatUtility.message().printClientModuleMessage("BEATING...")
            }

            mc.player.connection.sendPacket(CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, beatPos!!, EnumFacing.UP))

            isBeating = false
        }
    }

    private val receive = Listener<PacketEvent.Receive>(EventHook {
        if(player == null) {
            return@EventHook
        }

        val packet = it.packet

        if(packet is SPacketBlockChange) {
            var debug = "unknown"

            if(packet.blockPosition == beatPos) {
                debug = "heartbeat"

                player!!.update()
            } else {
                if(packet.blockPosition.y != 0) {
                    return@EventHook
                }

                for(chunk in player!!.lastRequestedChunks) {
                    if(chunk.getBlock(0, 0, 0) == packet.blockPosition) {
                        player!!.onCoordReceive(chunk)

                        debug = "primary chunk"

                        break
                    }
                }
            }

            if(this.debug.valBoolean) {
                ChatUtility.message().printClientModuleMessage("$debug ${packet.blockPosition.toString()} -> ${packet.blockState.block.localizedName}")
            }
        } else if(packet is SPacketRespawn) {
            if(player!!.dimension != packet.dimensionID) {
                player!!.onDimensionChange(packet.dimensionID)
            }
        }
    })


    fun print(
        message : String,
        handler : AbstractChatMessage
    ) {
        handler.printClientModuleMessage(
            message
        )
    }
}