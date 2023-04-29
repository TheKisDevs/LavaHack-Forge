package com.kisman.cc.features.module.combat.holefillerrewrite

import com.kisman.cc.util.Globals.mc
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos

/**
 * @author _kisman_
 * @since 10:36 of 29.10.2022
 */
class HolesList : ArrayList<BlockPos>() {
    fun addPosses(
        elements : Collection<BlockPos>,
        entityCheck : Boolean
    ) {
        if(entityCheck) {
            for (element in elements) {
                for (entity in mc.world.entityList) {
                    if (mc.world.getEntitiesInAABBexcluding(
                            entity,
                            AxisAlignedBB(element),
                            null
                        ).size == 0
                    ) {
                        super.add(element)
                    }
                }
            }
        } else {
            super.addAll(elements)
        }
    }
}