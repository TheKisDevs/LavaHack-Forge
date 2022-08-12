package com.kisman.cc.features.module.combat

import com.kisman.cc.features.module.Beta
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import net.minecraft.item.ItemBow
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.network.play.client.CPacketPlayerTryUseItem
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 15:31 of 07.08.2022
 */
@Beta
class Quiver : Module(
    "Quiver",
    "Helps with bow",
    Category.COMBAT
) {
    override fun update() {
        if(
            mc.player == null
            || mc.world == null
            || mc.player.heldItemMainhand.item !is ItemBow
            || !mc.player.isHandActive
            || mc.player.itemInUseMaxCount < 3
        ) {
            return
        }

        val oldPitch = mc.player.rotationPitch

        mc.player.rotationPitch = -90f

        mc.player.connection.sendPacket(CPacketPlayerDigging(
            CPacketPlayerDigging.Action.RELEASE_USE_ITEM,
            BlockPos.ORIGIN,
            mc.player.horizontalFacing
        ))
        mc.player.connection.sendPacket(CPacketPlayerTryUseItem(mc.player.getActiveHand()))
        mc.player.stopActiveHand()

        mc.player.rotationPitch = oldPitch
    }
}