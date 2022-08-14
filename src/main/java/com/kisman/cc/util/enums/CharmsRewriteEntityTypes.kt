package com.kisman.cc.util.enums

import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.player.EntityPlayer

/**
 * @author _kisman_
 * @since 18:05 of 11.07.2022
 */
enum class CharmsRewriteEntityTypes(
    val entityClass : Class<out Entity?>
) {
    Crystal(EntityEnderCrystal::class.java),
    Player(EntityPlayer::class.java),
    Monster(EntityMob::class.java),
    Animal(EntityAnimal::class.java);

    companion object {
        fun get(entity : Entity) : CharmsRewriteEntityTypes? {
            for(type in values()) {
                if(type.entityClass.isInstance(entity)) return type
            }
            return null
        }
    }
}