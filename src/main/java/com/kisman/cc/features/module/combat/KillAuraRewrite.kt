package com.kisman.cc.features.module.combat

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.entity.EntityUtil
import com.kisman.cc.util.entity.RotationSaver
import com.kisman.cc.util.enums.KillAuraWeapons
import com.kisman.cc.util.enums.RotationLogic
import com.kisman.cc.util.enums.SwingHands
import com.kisman.cc.util.enums.dynamic.RotationEnum
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
class KillAuraRewrite : Module(
    "KillAuraRewrite",
    "Rewrite version of KillAura",
    Category.COMBAT
) {
    private val logic = register(SettingGroup(Setting("Logic", this)))
    private val swap = register(logic.add(Setting("Swap", this, SwapEnum2.Swap.None)))
    private val rotation = register(logic.add(Setting("Rotation", this, RotationEnum.Rotation.None)))
    private val rotationLogic = register(logic.add(Setting("Rotation Logic", this, RotationLogic.Default).setVisible { rotation.valBoolean }))
    private val weapon = register(logic.add(Setting("Weapon", this, KillAuraWeapons.Sword)))
    private val shieldBreaker = register(logic.add(Setting("Shield Breaker", this, false)))
    private val swing = register(logic.add(Setting("Swing", this, SwingHands.PacketSwing)))

    private val ranges = register(SettingGroup(Setting("Ranges", this)))
    private val range = register(ranges.add(Setting("Range", this, 4.25, 1.0, 6.0, false)))
    private val wallRange = register(ranges.add(Setting("Wall Range", this, 3.0, 1.0, 6.0, false)))

    private val targets = register(SettingGroup(Setting("Targets", this)))
    private val players = register(targets.add(Setting("Players", this, true)))
    private val monsters = register(targets.add(Setting("Monsters", this, false)))
    private val passive = register(targets.add(Setting("Passive", this, false)))

    private val hit = register(SettingGroup(Setting("Hit", this)))
    private val resetCooldown = register(hit.add(Setting("Reset Cooldown", this, true)))
    private val packetAttack = register(hit.add(Setting("Packet Attack", this, false)))

    private val checks = register(SettingGroup(Setting("Checks", this)))
    private val cooldownCheck = register(checks.add(Setting("Cooldown Check", this, true)))
    private val ccOnlyCrits = register(checks.add(Setting("CC Only Crits", this, true).setVisible { cooldownCheck.valBoolean }))
    private val fallCheck = register(checks.add(Setting("FallDistance Check", this, false)))

    companion object {
        public var target : Entity? = null
    }

    init {
        setDisplayInfo { "[${if(target == null) "No targets" else target?.name }]" }
    }

    override fun update() {
        if(mc.player == null || mc.world == null || mc.player.isDead) return

        if(cooldownCheck.valBoolean) {
            if(mc.player.getCooledAttackStrength(0f) <= (if(ccOnlyCrits.valBoolean) 0.95f else 1f)) return
        }

        target = EntityUtil.getTarget(range.valFloat, wallRange.valFloat, players.valBoolean, passive.valBoolean, monsters.valBoolean)

        if(target == null) {
            return
        }

        val oldSlot = mc.player.inventory.currentItem
        val saver = RotationSaver().save()
        val swapper = swap.valEnum as SwapEnum2.Swap
        val rotator = rotation.valEnum as RotationEnum.Rotation

        val weaponSlot = getWeaponSlot()

        if(weaponSlot == -1 && weapon.valEnum != KillAuraWeapons.None) {
            return
        }

        if(weapon.valEnum == KillAuraWeapons.None && oldSlot != weaponSlot) {
            return
        }

        swapper.task.doTask(weaponSlot, false)
        rotator.taskR.doTask(rotator.taskCEntity.doTask(target?.entityId, rotationLogic.valEnum as RotationLogic), false)

        if(packetAttack.valBoolean) {
            mc.player.connection.sendPacket(CPacketUseEntity(target!!))
        } else {
            mc.playerController.attackEntity(mc.player, target!!)
        }

        when(swing.valEnum as SwingHands) {
            SwingHands.MainHand -> mc.player.swingArm(EnumHand.MAIN_HAND)
            SwingHands.OffHand -> mc.player.swingArm(EnumHand.OFF_HAND)
            SwingHands.PacketSwing -> mc.player.connection.sendPacket(CPacketAnimation())
        }

        if(resetCooldown.valBoolean) {
            mc.player.resetCooldown()
        }

        rotator.taskRFromSaver.doTask(saver, true)
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