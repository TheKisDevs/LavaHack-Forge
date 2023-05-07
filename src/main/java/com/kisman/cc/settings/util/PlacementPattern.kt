package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module
import com.kisman.cc.features.subsystem.subsystems.RotationSystem
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingEnum
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.entity.player.InventoryUtil
import com.kisman.cc.util.enums.HandModes
import com.kisman.cc.util.enums.dynamic.SwapEnum2
import com.kisman.cc.util.world.BlockUtil2
import com.kisman.cc.util.world.block.RESPAWN_ANCHOR
import com.kisman.cc.util.world.raytrace
import net.minecraft.block.Block
import net.minecraft.init.Items
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

/**
 * @author _kisman_
 * @since 17:55 of 15.01.2023
 */
open class PlacementPattern(
    module : Module,
    private val canSwitch : Boolean = false,
    private val canExclude : Boolean = false
) : AbstractPattern<PlacementPattern>(
    module
) {
    protected val packet = setupSetting(Setting("Packet", module, true))
    protected val rotate = setupSetting(Setting("Rotate", module, false))
    protected val raytrace = setupSetting(Setting("RayTrace", module, false))
    protected val hand = setupEnum(SettingEnum("Hand", module, HandModes.MainHand))
    protected val switch = setupEnum(SettingEnum("Switch", module, SwapEnum2.Swap.None))
    protected val excludes = setupGroup(SettingGroup(Setting("Excludes", module)))
    protected val newVersionExcludeAnchors = setupSetting(excludes.add(Setting("Exclude New Version Anchors", module, true).setTitle("1.16 Anchors")))

    override fun preInit() : PlacementPattern {
        if(group != null) {
            group!!.add(packet)
            group!!.add(rotate)
//            group!!.add(raytrace)
            group!!.add(hand)

            if(canSwitch) {
                group!!.add(switch)
            }

            if(canExclude) {
                group!!.add(excludes)
            }
        }

        return this
    }

    override fun init() : PlacementPattern {
        module.register(packet)
        module.register(rotate)
//        module.register(raytrace)
        module.register(hand)

        if(canSwitch) {
            module.register(switch)
        }

        if(canExclude) {
            module.register(excludes)
            module.register(newVersionExcludeAnchors)
        }

        return this
    }

    fun placeBlockSwitch(
        pos : BlockPos,
        block : Block
    ) {
        val slot = if(newVersionExcludeAnchors.valBoolean){
            InventoryUtil.findBlockExtendedExclude(block, 0, 9, RESPAWN_ANCHOR)
        } else {
            InventoryUtil.findBlockExtended(block, 0, 9)
        }

        placeBlockSwitch(pos, slot)
    }

    protected open fun placeCrystal(
        pos : BlockPos
    ) {
        val slot = InventoryUtil.findItem(Items.END_CRYSTAL, 0, 9)
        val old = mc.player.inventory.currentItem

        if(slot != -1 || mc.player.heldItemOffhand.item == Items.END_CRYSTAL) {
            val facing = raytrace(pos, raytrace.valBoolean, EnumFacing.UP)

            switch(slot, false)

            if(rotate.valBoolean) {
                RotationSystem.handleRotate(pos)
            }

            if(packet.valBoolean) {
                mc.player.connection.sendPacket(CPacketPlayerTryUseItemOnBlock(pos, facing, hand.valEnum.hand, 0f, 0f, 0f))
            } else {
                mc.playerController.processRightClickBlock(mc.player, mc.world, pos, facing, Vec3d(0.0, 0.0, 0.0), hand.valEnum.hand)
            }

            switch(old, true)
        }
    }

    fun placeBlockSwitch(
        pos : BlockPos,
        slot : Int
    ) {
        val oldSlot = mc.player.inventory.currentItem

        if(slot != -1) {
            switch(slot, false)
            place(pos)
            switch(oldSlot, true)
        }
    }

    fun place(
        pos : BlockPos
    ) {
        BlockUtil2.placeBlock(pos, hand.valEnum.hand, packet.valBoolean, raytrace.valBoolean, rotate.valBoolean)
    }

    fun switch(
        slot : Int,
        silent : Boolean
    ) {
        switch.valEnum.task.doTask(slot, silent)
    }
}