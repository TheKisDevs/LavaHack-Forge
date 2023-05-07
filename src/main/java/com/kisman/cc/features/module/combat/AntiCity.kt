package com.kisman.cc.features.module.combat

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.util.state
import com.kisman.cc.util.world.playerPosition
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityEnderCrystal
import net.minecraft.network.play.client.CPacketUseEntity
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 13:57 of 01.05.2023
 */
@ModuleInfo(
    name = "AntiCity",
    category = Category.COMBAT
)
class AntiCity : Module() {
    private val surround = register(Setting("Surround", this, true))
    private val cev = register(Setting("Cev", this, true))
    private val civ = register(Setting("Civ", this, true))
    private val delay = register(Setting("Delay", this, 100.0, 0.0, 1000.0, NumberType.TIME))

    private val timer = timer()

    private val offsetsSurround = listOf(
        BlockPos(1, 0, 0),
        BlockPos(-1, 0, 0),
        BlockPos(0, 0, 1),
        BlockPos(0, 0, -1)
    )

    private val offsetsCiv = listOf(
        BlockPos(1, 1, 0),
        BlockPos(-1, 1, 0),
        BlockPos(0, 1, 1),
        BlockPos(0, 1, -1),
        BlockPos(1, 1, 1),
        BlockPos(-1, 1, -1),
        BlockPos(1, 1, -1),
        BlockPos(-1, 1, 1)
    )

    private val offsetsCev = listOf(
        BlockPos(0, 2, 0)
    )

    override fun update() {
        if(mc.player == null || mc.world == null) {
            return
        }

        if(timer.passedMillis(delay.valLong)) {
            val centre = playerPosition()

            fun processOffset(
                pos : BlockPos
            ) : Entity? {
                val entities = mc.world.getEntitiesWithinAABBExcludingEntity(null, state(pos).getSelectedBoundingBox(mc.world, pos))

                for(entity in entities) {
                    if(entity is EntityEnderCrystal) {
                       return entity
                    }
                }

                return null
            }

            fun processOffsets(
                offsets : List<BlockPos>,
                centre : BlockPos,
                state : Boolean
            ) : Entity? {
                if(!state) {
                    return null
                }

                for(offset in offsets) {
                    val pos = centre.add(offset)
                    val entity = processOffset(pos)

                    if(entity != null) {
                        return entity
                    }
                }

                return null
            }

            val entity = processOffsets(offsetsSurround, centre, surround.valBoolean) ?: processOffsets(offsetsCev, centre, cev.valBoolean) ?: processOffsets(offsetsCiv, centre, civ.valBoolean)

            if(entity != null) {
                mc.player.connection.sendPacket(CPacketUseEntity(entity))

                timer.reset()
            }
        }
    }
}