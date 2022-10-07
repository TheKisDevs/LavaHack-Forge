package com.kisman.cc.features.module.player

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.enums.PearlBypassModes
import com.kisman.cc.util.world.playerPosition
import com.kisman.cc.util.world.sendInteractPacket
import net.minecraft.init.Items
import net.minecraft.network.play.client.CPacketPlayerTryUseItem
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * @author _kisman_
 * @since 19:54 of 06.10.2022
 */
class PearlBypass : Module(
    "PearlBypass",
    "something like pearl phase bypass",
    Category.PLAYER
) {
    private val mode = register(Setting("Mode", this, PearlBypassModes.Normal))

    @SubscribeEvent
    fun onRightClickBlock(
        event : RightClickBlock
    ) {
        if (mc.player != null && mc.world != null && mc.player.inventory.getStackInSlot(mc.player.inventory.currentItem).getItem() === Items.ENDER_PEARL) {
            if(mode.valEnum == PearlBypassModes.Normal) {
                mc.player.connection.sendPacket(CPacketPlayerTryUseItem(event.hand))
            } else if(mode.valEnum == PearlBypassModes.CrystalPvPcc) {
                sendInteractPacket(playerPosition())
                mc.player.connection.sendPacket(CPacketPlayerTryUseItem(event.hand))
            }

            event.isCanceled = true
        }
    }
}