package com.kisman.cc.util.client.providers

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.EventDamageBlock
import com.kisman.cc.event.events.EventPlayerMove
import com.kisman.cc.features.module.exploit.PacketMine
import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.entity.player.InventoryUtil
import com.kisman.cc.util.math.MathUtil
import com.kisman.cc.util.world.BlockUtil
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listenable
import me.zero.alpine.listener.Listener
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.network.play.client.CPacketAnimation
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

/**
 * @author _kisman_
 * @since 12:40 of 15.10.2022
 */
object PacketMineProvider : Listenable {
    var position : BlockPos? = null

    var posToMine : BlockPos? = null
    private var lastPosToMine : BlockPos? = null

    private var swapper = DefaultSwapper()

    var silent = false
    var instant = false
    var strict = false
    var speed = 0.0
    var range = 0
    var autoSwitch = false
    var instantAttempts = 0
    var packetSpam = 0

    var swap = false
    var strictCheck = false
    var checked = false
    var rebreakCount = 0
    var start = 0L
    var delay = 0
    var oldSlot = -1

    fun reset() {
        rebreakCount = 0
        oldSlot = -1
        position = null
        delay = 0
    }

    private val playerMove = Listener<EventPlayerMove>(EventHook {
        if(!active()) {
            return@EventHook
        }

        if(position != null) {
            if(instant) {
                if(mc.world.getBlockState(position!!).block == Blocks.AIR) {
                    if(!checked) {
                        rebreakCount = 0
                        checked = true
                        start = System.currentTimeMillis()
                        strictCheck = false
                    }
                } else {
                    if(strict && !strictCheck) {
                        val block = mc.world.getBlockState(position!!).block

                        if(!(block == Blocks.ENDER_CHEST || block == Blocks.ANVIL)) {
                            rebreakCount = 0
                            position = null
                            strictCheck = true
                            return@EventHook
                        }
                    }
                    checked = false
                }
            }

            if(blockProgressCheck() && mc.world.getBlockState(position!!).block != Blocks.AIR && autoSwitch && !swapper?.swap()!!) {
                return@EventHook
            }

            if(!swap) {
                oldSlot = mc.player.inventory.currentItem
            }

            if(position != null && mc.player.getDistanceSq(position!!) >= range.toDouble() * range.toDouble()) {
                position = null
            }
        }

        try {
            mc.playerController.blockHitDelay = 0
        } catch(_ : Exception) {}
    })

    private fun blockProgressCheck() : Boolean = getBlockProgress(
        position!!, mc.player.inventory.getStackInSlot(InventoryUtil.findBestToolSlot(
            position
        )), start
    ) <= 1 - speed

    private val damageBlock = Listener<EventDamageBlock>(EventHook {
        if(handleBlockClick(it.blockPos, it.faceDirection)) {
            it.cancel()
        }
    })

    fun handleBlockClick(
        pos : BlockPos,
        facing : EnumFacing?
    ) : Boolean {
        if(!active()) {
            return false
        }

        if(swap) {
            return true
        }

        if(BlockUtil.canBlockBeBroken(pos)) {
            if(position != null) {
                if(pos.toLong() == position!!.toLong()) {
                    if(!swap && blockProgressCheck() && mc.world.getBlockState(position!!).block != Blocks.AIR) {
                        if(silent) {
                            swapper.swap()
                        }

                        try {
                            mc.player.connection.sendPacket(CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, position!!, EnumFacing.DOWN))
                        } catch(_ : Exception) {}

                        return true
                    }

                    return false
                }

                mc.player.connection.sendPacket(CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, position!!, facing ?: EnumFacing.DOWN))
                mc.playerController.isHittingBlock = false
            }

            mc.player.connection.sendPacket(CPacketAnimation(EnumHand.MAIN_HAND))

            for(j in 0..packetSpam) {
                mc.player.connection.sendPacket(CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.DOWN))
            }

            mc.player.connection.sendPacket(CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.DOWN))

            position = pos
            start = System.currentTimeMillis()
            strictCheck = true

            return true
        }

        return false
    }

    @SubscribeEvent fun onClientTick(event : TickEvent.ClientTickEvent) {
        if(!active() || mc.player == null || mc.world == null || mc.player.connection == null) {
            lastPosToMine = null
            return
        }

        if(posToMine != lastPosToMine && posToMine != null) {
            handleBlockClick(posToMine!!, EnumFacing.UP)
        }

        if(swap) {
            mc.player.connection.sendPacket(CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, position!!, EnumFacing.DOWN))

            if(delay >= 2) {
                InventoryUtil.switchToSlot(oldSlot, false)
                swap = false

                if(!instant && position != null) {
                    position = null
                }

                delay = 0
            }

            delay++
        }

        lastPosToMine = posToMine
    }

    init {
        Kisman.EVENT_BUS.subscribe(playerMove)
        Kisman.EVENT_BUS.subscribe(damageBlock)
        MinecraftForge.EVENT_BUS.register(this)
    }

    private fun getBlockProgress(blockPos: BlockPos, stack: ItemStack, start: Long): Float {
        return MathUtil.clamp(
            1 - (System.currentTimeMillis() - start) / InventoryUtil.time(blockPos, stack).toDouble(),
            0.0,
            1.0
        ).toFloat()
    }

    private fun active() : Boolean = PacketMine.instance.isToggled
}

interface ISwapper {
    fun swap() : Boolean
}

class DefaultSwapper : ISwapper {
    override fun swap() : Boolean {
        if(PacketMineProvider.rebreakCount > PacketMineProvider.instantAttempts - 1 && PacketMineProvider.instantAttempts != 0) {
            PacketMineProvider.position = null
            PacketMineProvider.rebreakCount = 0

            return false
        }

        InventoryUtil.switchToSlot(InventoryUtil.findBestToolSlot(PacketMineProvider.position), false)

        if(PacketMineProvider.silent) {
            mc.player.connection.sendPacket(CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, PacketMineProvider.position!!, EnumFacing.DOWN))
            PacketMineProvider.rebreakCount++

            if(!PacketMineProvider.instant) {
                PacketMineProvider.position = null
            }

            InventoryUtil.switchToSlot(PacketMineProvider.oldSlot, false)
        } else {
            PacketMineProvider.swap = true
        }

        return true
    }
}