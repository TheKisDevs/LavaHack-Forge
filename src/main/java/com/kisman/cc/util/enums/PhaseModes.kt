package com.kisman.cc.util.enums

import com.kisman.cc.features.module.movement.Phase
import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.entity.player.InventoryUtil
import com.kisman.cc.util.world.playerPosition
import com.kisman.cc.util.world.sendInteractPacket
import net.minecraft.init.Items
import net.minecraft.network.play.client.CPacketHeldItemChange
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.client.CPacketPlayerTryUseItem
import net.minecraft.util.EnumHand

/**
 * @author _kisman_
 * @since 20:06 of 07.10.2022
 */
enum class PhaseModes(
    private val handler : IPhaseMode
) {
    Pearl (object : IPhaseMode {
            override fun update(
                phase : Phase
            ) {
                val slot = InventoryUtil.findItem(Items.ENDER_PEARL, 0, 9)
                val oldPitch = mc.player.rotationPitch


                if (mc.player.collidedHorizontally && slot != -1) {
                    mc.player.connection.sendPacket(CPacketPlayer.Rotation(mc.player.rotationYaw, 84f, mc.player.onGround))

                    if ( mc.player.heldItemMainhand.getItem().equals(Items.ENDER_PEARL) || mc.player.heldItemOffhand.getItem().equals(Items.ENDER_PEARL)) {
                        mc.player.connection.sendPacket(CPacketPlayerTryUseItem(EnumHand.MAIN_HAND))
                    } else {
                        val oldSlot = mc.player.inventory.currentItem
                        mc.player.connection.sendPacket(CPacketHeldItemChange(slot))
                        mc.player.connection.sendPacket(CPacketPlayerTryUseItem(EnumHand.MAIN_HAND))
                        mc.player.connection.sendPacket(CPacketHeldItemChange(oldSlot))
                    }

                    mc.player.connection.sendPacket(CPacketPlayer.Rotation(mc.player.rotationYaw, oldPitch, mc.player.onGround))

                    if(phase.autoDisable.valBoolean) {
                        phase.isToggled = false
                    }
                }
            }
        }
    ),
    PearlBypass (object : IPhaseMode {
            override fun update(
                phase : Phase
            ) {
                val slot = InventoryUtil.findItem(Items.ENDER_PEARL, 0, 9)
                val oldPitch = mc.player.rotationPitch

                if (mc.player.collidedHorizontally && slot != -1) {
                    sendInteractPacket(playerPosition())

                    mc.player.connection.sendPacket(CPacketPlayer.Rotation(mc.player.rotationYaw, 84f, mc.player.onGround))

                    if ( mc.player.heldItemMainhand.getItem().equals(Items.ENDER_PEARL) || mc.player.heldItemOffhand.getItem().equals(Items.ENDER_PEARL)) {
                        mc.player.connection.sendPacket(CPacketPlayerTryUseItem(EnumHand.MAIN_HAND))
                    } else {
                        val oldSlot = mc.player.inventory.currentItem
                        mc.player.connection.sendPacket(CPacketHeldItemChange(slot))
                        mc.player.connection.sendPacket(CPacketPlayerTryUseItem(EnumHand.MAIN_HAND))
                        mc.player.connection.sendPacket(CPacketHeldItemChange(oldSlot))
                    }

                    mc.player.connection.sendPacket(CPacketPlayer.Rotation(mc.player.rotationYaw, oldPitch, mc.player.onGround))

                    if(phase.autoDisable.valBoolean) {
                        phase.isToggled = false
                    }
                }
            }
        }
    )

    ;

    fun update(
        phase : Phase
    ) {
        handler.update(phase)
    }
}

interface IPhaseMode {
    fun update(
        phase : Phase
    )
}