package com.kisman.cc.features.module.misc.fakeplayer

import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.minecraft.receive
import com.mojang.authlib.GameProfile
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.entity.Entity
import net.minecraft.init.Items
import net.minecraft.init.MobEffects
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.network.play.server.SPacketEntityStatus
import net.minecraft.potion.PotionEffect
import net.minecraft.util.DamageSource
import net.minecraft.world.World

/**
 * @author _kisman_
 * @since 11:59 of 25.03.2023
 */
class EntityPoppable(
    world : World,
    profile : GameProfile
) : EntityOtherPlayerMP(
    world,
    profile
) {
    override fun markVelocityChanged() { }

    override fun getItemStackFromSlot(
        slot : EntityEquipmentSlot
    ) = if(slot == EntityEquipmentSlot.OFFHAND) {
        ItemStack(Items.TOTEM_OF_UNDYING).also {
            it.count = 1
        }
    } else {
        super.getItemStackFromSlot(slot)
    }

    override fun getTotalArmorValue() = mc.player.totalArmorValue
//    override fun getArmorToughness() = mc.player.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).attributeValue.toFloat()
//    override fun getExplosionModifier(
//        source : DamageSource
//    ) = EnchantmentHelper.getEnchantmentModifierDamage(armorInventoryList, source)

    override fun knockBack(
        entityIn : Entity,
        strength : Float,
        xRatio : Double,
        zRatio : Double
    ) { }

    override fun setHealth(
        health : Float
    ) = if(health <= 0f) {
        pop()
    } else {
        super.setHealth(health)
    }

    override fun attackEntityFrom(
        source : DamageSource,
        amount : Float
    ) : Boolean {
        health -= amount

        return super.attackEntityFrom(source, amount)
    }

    private fun pop() {
        receive(SPacketEntityStatus(this, 35.toByte()))
        health = 1f
        absorptionAmount = 8f
        clearActivePotions()
        addPotionEffect(PotionEffect(MobEffects.REGENERATION, 900, 1))
        addPotionEffect(PotionEffect(MobEffects.ABSORPTION, 100, 1))
    }
}