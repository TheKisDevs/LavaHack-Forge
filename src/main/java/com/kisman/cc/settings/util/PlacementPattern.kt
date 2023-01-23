package com.kisman.cc.settings.util

import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingEnum
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.entity.player.InventoryUtil
import com.kisman.cc.util.enums.HandModes
import com.kisman.cc.util.enums.dynamic.SwapEnum2
import com.kisman.cc.util.world.BlockUtil2
import com.kisman.cc.util.world.block.RESPAWN_ANCHOR
import net.minecraft.block.Block
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 17:55 of 15.01.2023
 */
class PlacementPattern(
    module : Module,
    private val canSwitch : Boolean = false,
    private val canExclude : Boolean = false
) : AbstractPattern<PlacementPattern>(
    module
) {
    private val packet = setupSetting(Setting("Packet", module, true))
    private val rotate = setupSetting(Setting("Rotate", module, false))
    private val raytrace = setupSetting(Setting("RayTrace", module, false))
    private val hand = setupEnum(SettingEnum("Hand", module, HandModes.MainHand))
    private val switch = setupEnum(SettingEnum("Switch", module, SwapEnum2.Swap.None))
    private val excludes = setupGroup(SettingGroup(Setting("Excludes", module)))
    private val newVersionExcludeAnchors = setupSetting(excludes.add(Setting("Exclude New Version Anchors", module, true).setTitle("1.16 Anchors")))

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

        val oldSlot = mc.player.inventory.currentItem

        if(slot != -1) {
            switch(slot, false)
            place(pos)
            switch(oldSlot, true)
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