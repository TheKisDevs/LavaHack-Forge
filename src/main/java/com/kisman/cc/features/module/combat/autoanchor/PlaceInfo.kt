package com.kisman.cc.features.module.combat.autoanchor

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 18:54 of 23.01.2023
 */
class PlaceInfo(
    val target : EntityPlayer,
    val anchorPos : BlockPos,
    val glowstonePos : BlockPos,
    val selfDamage : Float,
    val targetDamage : Float
)