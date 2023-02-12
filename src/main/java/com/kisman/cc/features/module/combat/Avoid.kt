package com.kisman.cc.features.module.combat

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.WorkInProgress
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingEnum
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.util.TimerUtils
import com.kisman.cc.util.enums.AvoidModes
import com.kisman.cc.util.enums.dynamic.SwapEnum2
import com.kisman.cc.util.movement.MovementUtil
import com.kisman.cc.util.world.playerPosition
import com.kisman.cc.util.world.raytrace
import net.minecraft.init.Blocks
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos

/**
 * TODO: replace - will help with anchors
 * TODO: entity check
 *
 * @author _kisman_
 * @since 21:35 of 27.01.2023
 */
@WorkInProgress
class Avoid : Module(
    "Avoid",
    "Avoids auto trap/anchor aura by placing crystals",
    Category.COMBAT
) {
    private val mode = register(Setting("Mode", this, AvoidModes.AutoTrap))
    private val swap = register(SettingEnum("Swap", this, SwapEnum2.Swap.None))
    private val delay = register(Setting("Delay", this, 100.0, 0.0, 10000.0, NumberType.TIME))
    private val spam = register(Setting("Spam", this, false))
    private val raytrace = register(Setting("RayTrace", this, false))
    private val whileMove = register(Setting("While Move", this, false))
//    private val newVersionPlacement = register(Setting("New Version Placement", this, false).setTitle("1.13+"))

    private val timer = TimerUtils()

    init {
        super.setDisplayInfo { "[${mode.valEnum}]" }
    }

    override fun onEnable() {
        timer.reset()
    }

    override fun update() {
        if(mc.player == null || mc.world ==  null || (!whileMove.valBoolean && MovementUtil.isMoving())) {
            return
        }

        val playerPos = playerPosition()

        var eastPos = playerPos.offset(EnumFacing.EAST)
        var westPos = playerPos.offset(EnumFacing.WEST)
        var northPos = playerPos.offset(EnumFacing.NORTH)
        var southPos = playerPos.offset(EnumFacing.SOUTH)

        if(mode.valEnum == AvoidModes.AnchorAura) {
            eastPos = eastPos.up()
            westPos = westPos.up()
            northPos = northPos.up()
            southPos = southPos.up()
        }

        fun valid(
            pos : BlockPos
        ) : Boolean = mc.world.getBlockState(pos).block == Blocks.OBSIDIAN
                && mc.world.getBlockState(pos.up()).block == Blocks.AIR
                && mc.world.getBlockState(pos.up(2)).block == Blocks.AIR

        fun findPos(
            vararg posses : BlockPos
        ) : BlockPos? {
            for(pos in posses) {
                if(valid(pos)) {
                    return pos
                }
            }

            return null
        }

        val placePos = findPos(eastPos, westPos, northPos, southPos)

        if(placePos != null) {
            if (spam.valBoolean) {
                mc.player.connection.sendPacket(CPacketPlayerTryUseItemOnBlock(placePos, raytrace(placePos, raytrace.valBoolean, EnumFacing.UP), EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f))
                mc.player.swingArm(EnumHand.MAIN_HAND)
            }
        }
    }
}