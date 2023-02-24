package com.kisman.cc.features.module.combat

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.subsystem.subsystems.*
import com.kisman.cc.features.subsystem.subsystems.Target
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.entity.EntityUtil
import com.kisman.cc.util.entity.RotationSaver
import com.kisman.cc.util.enums.KillAuraWeapons
import com.kisman.cc.util.enums.SwingHands
import com.kisman.cc.util.enums.dynamic.SwapEnum2
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.ItemAxe
import net.minecraft.item.ItemSword
import net.minecraft.network.play.client.CPacketAnimation
import net.minecraft.network.play.client.CPacketUseEntity
import net.minecraft.util.EnumHand

/**
 * @author _kisman_
 * @since 11:23 of 06.06.2022
 */
@Targetable
@TargetsNearest
class KillAuraRewrite : Module(
    "KillAuraRewrite",
    "Rewrite version of KillAura",
    Category.COMBAT
) {
    private val logic = register(SettingGroup(Setting("Logic", this)))
    private val swap = register(logic.add(Setting("Swap", this, SwapEnum2.Swap.None)))
    private val rotate = register(logic.add(Setting("Rotate", this, false)))
    private val weapon = register(logic.add(Setting("Weapon", this, KillAuraWeapons.Sword)))
    private val shieldBreaker = register(logic.add(Setting("Shield Breaker", this, false)))
    private val swing = register(logic.add(Setting("Swing", this, SwingHands.PacketSwing)))

    private val hit = register(SettingGroup(Setting("Hit", this)))
    private val resetCooldown = register(hit.add(Setting("Reset Cooldown", this, true)))
    private val packetAttack = register(hit.add(Setting("Packet Attack", this, false)))

    private val checks = register(SettingGroup(Setting("Checks", this)))
    private val cooldownCheck = register(checks.add(Setting("Cooldown Check", this, true)))
    private val ccOnlyCrits = register(checks.add(Setting("CC Only Crits", this, true).setVisible { cooldownCheck.valBoolean }))

    companion object {
        @JvmStatic var instance : KillAuraRewrite? = null
        var target : Entity? = null
    }

    init {
        setDisplayInfo { "[${if(target == null) "no target no fun" else target?.name }]" }

        displayName = "KillAura"
        instance = this
    }

    override fun update() {
        if(mc.player == null || mc.world == null || mc.player.isDead) return

        val oldSlot = mc.player.inventory.currentItem
        val swapper = swap.valEnum as SwapEnum2.Swap
        val weaponSlot = getWeaponSlot()

        if(cooldownCheck.valBoolean && oldSlot != weaponSlot && swapper == SwapEnum2.Swap.None) {
            if(mc.player.getCooledAttackStrength(0f) <= (if(ccOnlyCrits.valBoolean) 0.95f else 1f)) {
                return
            }
        }

        target = nearest()

        if(target == null || oldSlot == -1 || (weaponSlot != oldSlot && swapper == SwapEnum2.Swap.None)) {
            return
        }

        swapper.task.doTask(weaponSlot, false)

        if(rotate.valBoolean) {
            RotationSystem.handleRotate(target!!)
        }

        if(cooldownCheck.valBoolean && oldSlot == weaponSlot && swapper != SwapEnum2.Swap.None) {
            if(mc.player.getCooledAttackStrength(0f) > (if(ccOnlyCrits.valBoolean) 0.95f else 1f)) {
                if(packetAttack.valBoolean) {
                    mc.player.connection.sendPacket(CPacketUseEntity(target!!))
                } else {
                    mc.playerController.attackEntity(mc.player, target!!)
                }

                when(swing.valEnum as SwingHands) {
                    SwingHands.MainHand -> mc.player.swingArm(EnumHand.MAIN_HAND)
                    SwingHands.OffHand -> mc.player.swingArm(EnumHand.OFF_HAND)
                    SwingHands.PacketSwing -> mc.player.connection.sendPacket(CPacketAnimation(EnumHand.MAIN_HAND))
                }

                if(resetCooldown.valBoolean) {
                    mc.player.resetCooldown()
                }
            }
        }

        swapper.task.doTask(oldSlot, true)
    }

    private fun getWeaponSlot() : Int {
        for(i in 0..9) {
            val stack = mc.player.inventory.getStackInSlot(i)

            if(shieldBreaker.valBoolean && isShieldActive(target!!)) {
                if(stack.item is ItemAxe) {
                    return i
                }
            }

            if(weapon.valEnum != KillAuraWeapons.None) {
                if(
                    (stack.item is ItemSword && (weapon.valEnum == KillAuraWeapons.Sword || weapon.valEnum == KillAuraWeapons.Both))
                    || (stack.item is ItemAxe && (weapon.valEnum == KillAuraWeapons.Axe || weapon.valEnum == KillAuraWeapons.Both))
                ) {
                    return i
                }
            }
        }

        return -1
    }

    private fun isShieldActive(entity : Entity) : Boolean {
        if(entity is EntityPlayer && entity.isHandActive) {
            if(
                (entity.heldItemMainhand.item == Items.SHIELD && entity.activeHand == EnumHand.MAIN_HAND)
                || (entity.heldItemOffhand.item == Items.SHIELD && entity.activeHand == EnumHand.OFF_HAND)
            ) {
                return true
            }
        }
        return false
    }
}