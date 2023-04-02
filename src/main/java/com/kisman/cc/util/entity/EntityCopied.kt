package com.kisman.cc.util.entity

import com.kisman.cc.util.client.interfaces.IFakeEntity
import com.mojang.authlib.GameProfile
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.world.World

/**
 * @author _kisman_
 * @since 7:59 of 02.04.2023
 */
open class EntityCopied(
    world : World,
    profile : GameProfile,
    protected val player : EntityPlayer
) : EntityOtherPlayerMP(
    world,
    profile
), IFakeEntity {
    private val swing = player.limbSwing
    private val amount = player.limbSwingAmount
    private val progress = player.swingProgress
    private val amountPrev = player.prevLimbSwingAmount
    private val progressPrev = player.prevSwingProgress
    private val mainhand = player.heldItemMainhand!!
    private val offhand = player.heldItemOffhand!!

    init {
        sync()
    }

    protected fun sync() {
        limbSwing = swing
        limbSwingAmount = amount
        swingProgress = progress
        prevLimbSwingAmount = amountPrev
        prevSwingProgress = progressPrev
    }

    override fun onUpdate() {
        super.onUpdate()

        sync()
    }

    override fun isSpectator() = false
    override fun isCreative() = false

    override fun getItemStackFromSlot(
        slot : EntityEquipmentSlot
    ) = when(slot) {
        EntityEquipmentSlot.MAINHAND -> mainhand
        EntityEquipmentSlot.OFFHAND -> offhand
        else -> super.getItemStackFromSlot(slot)!!
    }

    override fun showNameTag() = false
}