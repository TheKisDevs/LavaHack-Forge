package com.kisman.cc.features.nocom.modules

import com.kisman.cc.event.events.PacketEvent
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.SubscribeMode
import com.kisman.cc.features.module.Subscribes
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.chat.cubic.ChatUtility
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.network.play.server.SPacketBlockChange
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent
import java.text.DecimalFormat

/**
 * @author _kisman_
 * @since 0:48 of 28.08.2022
 */
@Subscribes(
    mode = SubscribeMode.ALPINE
)
class NoComMainModule : Module(
    "NoCom",
    "Main module of nocom",
    null
) {
    private val x = register(Setting("X", this, 0.0, -1000.0, 1000.0, true))
    private val y = register(Setting("Y", this, 0.0, -1000.0, 1000.0, true))
    private val z = register(Setting("Z", this, 0.0, -1000.0, 1000.0, true))

    private val latency = register(Setting("Latency", this, false))
    private val ignore = register(Setting("Ignore Loaded", this, true))
    private val loadChunks = register(Setting("Load Chunk", this, false))

    private val notify = register(Setting("Notify", this, false))


    private val loadedChunks = ArrayList<ChunkPos>()

    companion object {
        @JvmStatic val MAX_DL_PPT = 15
    }

    private var startTime = 0L

    private val receive = Listener<PacketEvent.Receive>(EventHook {
        val packet = it.packet

        if(packet is SPacketBlockChange) {
            val chunkPos = ChunkPos(packet.blockPosition)

            if(ignore.valBoolean) {
                if(!loadedChunks.contains(chunkPos) && mc.world.isBlockLoaded(packet.blockPosition, false)) {
                    return@EventHook
                }

                if(notify.valBoolean) {
                    ChatUtility.warning().printClientModuleMessage("NoCom found something")
                }
            }

            val format = DecimalFormat("#.#")
            val pos = Vec3d(
                mc.player.posX,
                packet.blockPosition.y.toDouble(),
                mc.player.posZ
            )

            ChatUtility.complete().printClientModuleMessage("${packet.blockPosition}->${packet.blockState.block.localizedName} (${format.format(pos.distanceTo(Vec3d(packet.blockPosition)))}) ${if(mc.player.dimension == -1) "Nether" else if(mc.player.dimension == 1) "End" else "Overworld"}")

            if(latency.valBoolean && startTime != -1L) {
                ChatUtility.message().printClientModuleMessage("Latency = ${System.currentTimeMillis() - startTime} ms")

                startTime = -1L
            }

            if(loadChunks.valBoolean && ignore.valBoolean && !loadedChunks.contains(chunkPos)) {
                mc.world.doPreChunk(
                    chunkPos.x,
                    chunkPos.z,
                    true
                )

                loadedChunks.add(chunkPos)
            }
        }
    })

    override fun onEnable() {
        super.onEnable()

        if(mc.player == null || mc.world == null) {
            return
        }

        val pos = BlockPos(
            x.valInt,
            y.valInt,
            z.valInt
        )

        startTime = System.currentTimeMillis()

        mc.player.connection.sendPacket(CPacketPlayerDigging(
            CPacketPlayerDigging.Action.START_DESTROY_BLOCK,
            pos,
            EnumFacing.UP
        ))
    }

    override fun onDisable() {
        super.onDisable()

        startTime = -1
        unloadChunks()
    }

    /*@SubscribeEvent*/ private fun onConnect(event : FMLNetworkEvent.ClientConnectedToServerEvent) {
        unloadChunks()
    }

    private fun unloadChunks() {
        if(mc.world != null) {
            for(chunkPos in loadedChunks) {
                mc.world.doPreChunk(
                    chunkPos.x,
                    chunkPos.z,
                    false
                )
            }
        }

        loadedChunks.clear()
    }
}