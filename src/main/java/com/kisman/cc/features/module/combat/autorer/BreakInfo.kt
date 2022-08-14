package com.kisman.cc.features.module.combat.autorer

import net.minecraft.entity.item.EntityEnderCrystal

/**
 * @author _kisman_
 * @since 18:45 of 11.08.2022
 */
class BreakInfo(
    var crystal : EntityEnderCrystal,
    var selfDamage : Float,
    var targetDamage : Float,
    var ignoreDamageSync : Boolean
)