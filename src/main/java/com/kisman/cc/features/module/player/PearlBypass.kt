package com.kisman.cc.features.module.player

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
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
    "cc phase bypass frfr",
    Category.PLAYER
) {
    @SubscribeEvent
    fun onRightClickBlock(
        event : RightClickBlock
    ) {
        if (mc.player == null || mc.world == null) return
        if (mc.player.inventory.getStackInSlot(mc.player.inventory.currentItem)
                .getItem() === Items.ENDER_PEARL
        ) {
            mc.player.connection.sendPacket(CPacketPlayerTryUseItem(event.hand))
            event.isCanceled = true
        }
    }
}