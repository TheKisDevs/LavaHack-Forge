package com.kisman.cc.features.subsystem.subsystems

import com.kisman.cc.features.subsystem.SubSystem
import com.kisman.cc.util.TimerUtils
import com.kisman.cc.util.world.entityPosition
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.gameevent.TickEvent

/**
 * @author _kisman_
 * @since 8:09 of 25.02.2023
 */
object CrystalProcessor : SubSystem(
    "Crystal Processor"
) {
    private val blocks = mutableListOf<BlockPos>()

    private val blockStatuses = mutableMapOf<BlockPos, Boolean>()
    private val freeBlocks = mutableListOf<BlockPos>()
    private val crystalBlocks = mutableMapOf<BlockPos, Pair<EntityEnderCrystal, Long>>()
    private var prevBlockStatuses = mutableMapOf<BlockPos, Boolean>()
    private var prevFreeBlocks = mutableListOf<BlockPos>()
    private var prevCrystalBlocks = mutableMapOf<BlockPos, Pair<EntityEnderCrystal, Long>>()

    private val foundedCrystals = mutableMapOf<BlockPos, Int>()

    private val delays = mutableMapOf<BlockPos, Int>()

    private val timer = TimerUtils()

    fun add(
        pos : BlockPos
    ) {
        blocks.add(pos)
    }

    fun remove(
        pos : BlockPos
    ) {
        blocks.remove(pos)
    }

    override fun update(
        event : TickEvent.ClientTickEvent
    ) {
        if(timer.passedMillis(1000L)) {
            delays.clear()

            for(pos in foundedCrystals.entries) {
                delays[pos.key] = 1000 / pos.value
            }

            timer.reset()
        }

        blockStatuses.clear()
        freeBlocks.clear()
        crystalBlocks.clear()

        for(entity in mc.world.loadedEntityList) {
            if(entity is EntityEnderCrystal) {
                val entityPos = entityPosition(entity).down()

                for(pos in blocks) {
                    if(entityPos == pos) {
                        crystalBlocks[pos] = Pair(entity, System.currentTimeMillis())
                    }
                }
            }
        }

        for(pos in blocks) {
            if(!crystalBlocks.containsKey(pos)) {
                freeBlocks.add(pos)
            }
        }

        for(pos in blocks) {
            blockStatuses[pos] = crystalBlocks.contains(pos)
        }

        prevBlockStatuses = blockStatuses
        prevFreeBlocks = freeBlocks
        prevCrystalBlocks = crystalBlocks

        for(pos in crystalBlocks.keys) {
            foundedCrystals[pos] = (if(foundedCrystals.containsKey(pos)) foundedCrystals[pos]!! else 0) + 1
        }
    }
}