package com.kisman.cc.util.minecraft

import baritone.api.BaritoneAPI
import baritone.api.event.events.BlockInteractEvent
import com.kisman.cc.Kisman
import com.kisman.cc.util.Globals.mc
import net.minecraft.block.material.Material
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.math.RayTraceResult
import net.minecraftforge.common.ForgeHooks

/**
 * @author _kisman_
 * @since 13:12 of 23.08.2022
 */

fun leftClick() {
    if(mc.leftClickCounter <= 0) {
        if(mc.objectMouseOver == null) {
            error("Null returned as 'hitResult', this shouldn't happen!")

            if(mc.playerController.isNotCreative) {
                mc.leftClickCounter = 10
            }
        } else if(!mc.player.isRowingBoat) {
            when(mc.objectMouseOver.typeOfHit!!) {
                RayTraceResult.Type.ENTITY -> mc.playerController.attackEntity(mc.player, mc.objectMouseOver.entityHit)
                RayTraceResult.Type.BLOCK -> {
                    BaritoneAPI.getProvider().primaryBaritone.gameEventHandler.onBlockInteract(
                        BlockInteractEvent(
                            mc.objectMouseOver.blockPos,
                            BlockInteractEvent.Type.START_BREAK
                        )
                    )

                    if(!mc.world.isAirBlock(mc.objectMouseOver.blockPos)) {
                        mc.playerController.clickBlock(mc.objectMouseOver.blockPos, mc.objectMouseOver.sideHit)
                    }
                }
                RayTraceResult.Type.MISS -> {
                    if(mc.playerController.isNotCreative) {
                        mc.leftClickCounter = 10
                    }

                    mc.player.resetCooldown()

                    ForgeHooks.onEmptyLeftClick(mc.player)
                }
            }

            mc.player.swingArm(EnumHand.MAIN_HAND)
        }
    }
}

fun rightClick() {
    if (!mc.playerController.getIsHittingBlock()) {
        mc.rightClickDelayTimer = 4

        if (!mc.player.isRowingBoat) {
            if (mc.objectMouseOver == null) {
                error("Null returned as 'hitResult', this shouldn't happen!")
            }

            for (hand in EnumHand.values()) {
                val itemstack = mc.player.getHeldItem(hand)
                if (mc.objectMouseOver != null) {
                    when (mc.objectMouseOver.typeOfHit!!) {
                        RayTraceResult.Type.ENTITY -> {
                            if (mc.playerController.interactWithEntity(
                                    mc.player,
                                    mc.objectMouseOver.entityHit,
                                    mc.objectMouseOver,
                                    hand
                                ) == EnumActionResult.SUCCESS
                            ) {
                                return
                            }
                            if (mc.playerController.interactWithEntity(
                                    mc.player,
                                    mc.objectMouseOver.entityHit,
                                    hand
                                ) == EnumActionResult.SUCCESS
                            ) {
                                return
                            }
                        }

                        RayTraceResult.Type.BLOCK -> {
                            if (mc.world.getBlockState(mc.objectMouseOver.blockPos).material !== Material.AIR) {
                                val i = itemstack.count
                                val result = mc.playerController.processRightClickBlock(
                                    mc.player,
                                    mc.world,
                                    mc.objectMouseOver.blockPos,
                                    mc.objectMouseOver.sideHit,
                                    mc.objectMouseOver.hitVec,
                                    hand
                                )

                                if (result == EnumActionResult.SUCCESS) {
                                    mc.player.swingArm(hand)
                                    if (!itemstack.isEmpty() && (itemstack.count != i || mc.playerController.isInCreativeMode)) {
                                        mc.entityRenderer.itemRenderer.resetEquippedProgress(hand)
                                    }
                                    return
                                }
                            }
                        }

                        else -> {}
                    }
                }

                if (itemstack.isEmpty() && (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit == RayTraceResult.Type.MISS)) {
                    ForgeHooks.onEmptyClick(mc.player, hand)
                }

                if (!itemstack.isEmpty() && mc.playerController.processRightClick(
                        mc.player,
                        mc.world,
                        hand
                    ) == EnumActionResult.SUCCESS
                ) {
                    mc.entityRenderer.itemRenderer.resetEquippedProgress(hand)
                    return
                }
            }
        }
    }
}

fun middleClick() {
    if(mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit != RayTraceResult.Type.MISS) {
        ForgeHooks.onPickBlock(mc.objectMouseOver, mc.player, mc.world)
    }
}

private fun error(
    text : String
) {
    Kisman.LOGGER.error(text)
}