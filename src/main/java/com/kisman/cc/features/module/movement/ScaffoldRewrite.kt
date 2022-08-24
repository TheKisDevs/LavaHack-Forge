package com.kisman.cc.features.module.movement

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.SettingEnum
import com.kisman.cc.util.chat.cubic.ChatUtility
import com.kisman.cc.util.collections.Pair
import com.kisman.cc.util.entity.player.InventoryUtil
import com.kisman.cc.util.enums.dynamic.SwapEnum2
import com.kisman.cc.util.world.BlockUtil
import com.kisman.cc.util.world.BlockUtil2
import net.minecraft.block.Block
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import java.util.*

/**
 * @author _kisman_
 * @since 0:22 of 09.08.2022
 */
class ScaffoldRewrite : Module(
    "ScaffoldRewrite",
    "ohhhh",
    Category.MOVEMENT
) {
//    private val debug1 = register(Setting("Debug1", this, true))
    private val debug2 = register(Setting("Debug2", this, true))
//    private val debug3 = register(Setting("Debug3", this, true))
    private val tower = register(Setting("Tower", this, false))
    private val swap = SettingEnum("Swap", this, SwapEnum2.Swap.Silent).register()
    private val packet = register(Setting("Packet", this, false))

    private var lastY = 0.0

    override fun onEnable() {
        if (mc.player == null || mc.world == null) {
            toggle()
            return
        }
        lastY = mc.player.posY
        mc.player.jump()
//        oldPlayerPos = Vec3d(mc.player.posX, mc.player.posY, mc.player.posZ)
    }

    /*override fun onDisable() {
        oldPlayerPos = null
    }*/

    override fun update() {
        if (mc.player == null || mc.world == null) return

        var slot = -1
        val oldSlot = mc.player.inventory.currentItem
        var needToSwap = true

        if(
            !Block.getBlockFromItem(mc.player.heldItemOffhand.item).defaultState.isFullBlock
            && !Block.getBlockFromItem(mc.player.heldItemMainhand.item).defaultState.isFullBlock
        ) {
            slot = InventoryUtil.findValidScaffoldBlockHotbarSlot()

            if(slot == -1) {
                ChatUtility.error().printClientModuleMessage("No blocks! Disabling!")
                super.setToggled(false)
                return
            }
        } else {
            needToSwap = false
        }


/*if (needToSwap) {
                swap.valEnum.task.doTask(slot, false)
            }

            if (mc.world.getBlockState(pos.down()).block.isReplaceable(mc.world, pos.down())) {
                BlockUtil.placeBlock2(
                    BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ).down(),
                    EnumHand.MAIN_HAND,
                    false,
                    packet.valBoolean
                )
            }*/

        //BlockPos oldPos = new BlockPos(oldPlayerPos).down();

        if (needToSwap) {
            swap.valEnum.task.doTask(slot, false)
        }

        if(isTowerActive()) {
            if (BlockUtil2.isPositionPlaceable(mc.player.position.down(), false, true)) {
                BlockUtil.placeBlock2(
                    mc.player.position.down(),
                    EnumHand.MAIN_HAND,
                    false,
                    packet.valBoolean
                )
            }

            if (mc.player.onGround) {
                mc.player.jump()
                lastY = mc.player.posY
            } else {
                mc.player.motionY = -0.28
            }
        } else {
            val queue : Queue<BlockPos> = LinkedList()

            val oldPos = BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ).down()

            val playerPos = BlockPos(
                mc.player.posX + mc.player.motionX,
                mc.player.posY,
                mc.player.posZ + mc.player.motionZ
            ).down()

            if (!connected(oldPos, playerPos) && debug2.valBoolean) {
                addConnectingBlocks(oldPos, playerPos, queue)
            }

            queue.add(playerPos)

            for (pos in queue) {
                if (!BlockUtil2.isPositionPlaceable(pos, false, true)) continue
                BlockUtil.placeBlock2(
                    pos,
                    EnumHand.MAIN_HAND,
                    false,
                    packet.valBoolean
                )
            }
        }

        if (needToSwap) {
            swap.valEnum.task.doTask(oldSlot, true)
        }
    }

    private fun isTowerActive() : Boolean = tower.valBoolean && mc.player.posY.toInt() > lastY

    private fun connected(pos1 : BlockPos, pos2 : BlockPos) : Boolean {
        return listOf(
            pos1,
            pos1.north(),
            pos1.east(),
            pos1.south(),
            pos1.west()
        ).contains(pos2)
    }

    private fun addConnectingBlocks(pos1 : BlockPos, pos2 : BlockPos, queue : Queue<BlockPos>) {
        val pair = getPossibleConnectingFacings(pos1, pos2)
//        ChatUtility.info().printClientModuleMessage(pair.first.toString() + " / " + pair.second.toString())
        val p1 = pos1.offset(pair.first)
        val p2 = pos1.offset(pair.second)
        if (mc.player.getDistanceSq(p1.x + 0.5, p1.y.toDouble(), p1.z + 0.5) < mc.player.getDistanceSq(
                p2.x + 0.5,
                p2.y.toDouble(),
                p2.z + 0.5
            )
        ) queue.add(p1) else queue.add(p2)
        if (pos1.y < pos2.y) queue.add(pos2.down())
    }

    private fun getPossibleConnectingFacings(pos1 : BlockPos, pos2 : BlockPos) : Pair<EnumFacing> {
        val possibleFacings : MutableList<EnumFacing> = ArrayList()
        if (pos1.x < pos2.x) possibleFacings.add(EnumFacing.EAST) else possibleFacings.add(EnumFacing.WEST)
        if (pos1.z < pos2.z) possibleFacings.add(EnumFacing.SOUTH) else possibleFacings.add(EnumFacing.NORTH)
        return Pair(possibleFacings[0], possibleFacings[1])
    }
}