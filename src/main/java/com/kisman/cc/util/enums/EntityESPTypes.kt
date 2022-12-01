package com.kisman.cc.util.enums

import net.minecraft.entity.Entity
import net.minecraft.entity.item.*
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.player.EntityPlayer

enum class EntityESPTypes(
        val entityClass : Class<out Entity?>
) {
    Player(EntityPlayer::class.java),
    Crystal(EntityEnderCrystal::class.java),
    Monster(EntityMob::class.java),
    Animal(EntityAnimal::class.java),
    Item(EntityItem::class.java),
    XPOrb(EntityXPOrb::class.java),
    ItemFrame(EntityItemFrame::class.java),
    ArmorStand(EntityArmorStand::class.java)

    ;


    companion object {
        fun get(entity : Entity) : EntityESPTypes? {
            for(type in values()) {
                if(type.entityClass.isInstance(entity)) return type
            }
            return null
        }
    }
}