package com.kisman.cc.features.module.combat

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.PacketEvent
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.util.ObbyPlacementPattern
import com.kisman.cc.util.block
import com.kisman.cc.util.world.playerPosition
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.init.Blocks
import net.minecraft.network.play.server.SPacketBlockBreakAnim
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 10:14 of 13.05.2023
 */
@ModuleInfo(
    name = "BlockerRewrite",
    display = "Blocker",
    wip = true
)
class BlockerRewrite : Module() {
    private val extend = register(Setting("Extend", this, false))
    private val face = register(Setting("Face", this, false))

    private val placer = ObbyPlacementPattern(this, true).preInit().init()

    override fun onEnable() {
        super.onEnable()

        Kisman.EVENT_BUS.subscribe(receive)
    }

    override fun onDisable() {
        super.onDisable()

        Kisman.EVENT_BUS.unsubscribe(receive)
    }

    private val receive = Listener<PacketEvent.Receive>(EventHook {
        val packet = it.packet

        if(packet is SPacketBlockBreakAnim) {
            val pos = packet.position
            val block = block(pos)

            if(block != Blocks.BEDROCK && block != Blocks.AIR) {
                val playerPos = playerPosition()
                var placePos : BlockPos? = null

                if (extend.valBoolean) {
                    if (pos.equals(playerPos.north()))
                        placePos = (playerPos.north().north());

                    if (pos.equals(playerPos.east()))
                        placePos = (playerPos.east().east());

                    if (pos.equals(playerPos.west()))
                        placePos = (playerPos.west().west());

                    if (pos.equals(playerPos.south()))
                        placePos = (playerPos.south().south());
                }

                if (face.valBoolean) {
                    if (pos.equals(playerPos.north()))
                        placePos = (playerPos.north().add(0, 1, 0));

                    if (pos.equals(playerPos.east()))
                        placePos = (playerPos.east().add(0, 1, 0));

                    if (pos.equals(playerPos.west()))
                        placePos = (playerPos.west().add(0, 1, 0));

                    if (pos.equals(playerPos.south()))
                        placePos = (playerPos.south().add(0, 1, 0));
                }

                if(placePos != null) {
                    placer.placeBlockSwitch(placePos, Blocks.OBSIDIAN)
                }
            }
        }
    })
}