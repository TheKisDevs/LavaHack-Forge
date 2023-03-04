package com.kisman.cc.util.enums

import com.kisman.cc.util.manager.friend.FriendManager
import net.minecraft.entity.Entity
import net.minecraft.entity.item.*
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.player.EntityPlayer

/**
 * @author _kisman_
 * @since 16:56 of 04.03.2023
 */
enum class TracersEntityTypes(
    val entity : Class<out Entity>,
    val check : (Entity) -> (Boolean) = { true }
) {
    Players(EntityPlayer::class.java),
    Friend(EntityPlayer::class.java, { it is EntityPlayer && FriendManager.instance.isFriend(it) }),
    Crystal(EntityEnderCrystal::class.java),
    Monster(EntityMob::class.java),
    Animal(EntityAnimal::class.java),
    Item(EntityItem::class.java),
    XPOrb(EntityXPOrb::class.java),
    ItemFrame(EntityItemFrame::class.java),
    ArmorStand(EntityArmorStand::class.java)

    ;

    companion object {
        fun get(
            entity : Entity
        ) : TracersEntityTypes? {
            for(type in values()) {
                if(type.entity.isInstance(entity) && type.check(entity)) {
                    return type
                }
            }

            return null
        }
    }
}