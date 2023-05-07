package com.kisman.cc.features.module.combat.autorer

import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.math.BlockPos

class PlaceInfo(
    var target : EntityLivingBase?,
    var blockPos : BlockPos?,
    var selfDamage : Float,
    var targetDamage : Float,
    @JvmField var canPlace : Boolean
)