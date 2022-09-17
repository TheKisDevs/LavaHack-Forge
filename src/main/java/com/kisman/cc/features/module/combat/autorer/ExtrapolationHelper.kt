package com.kisman.cc.features.module.combat.autorer

import com.kisman.cc.mixin.accessors.IEntityPlayer
import com.kisman.cc.settings.util.MultiThreaddableModulePattern
import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.thread.kisman.ThreadHandler
import net.minecraft.entity.player.EntityPlayer
import java.util.function.Supplier

/**
 * @author _kisman_
 * @since 15:11 of 17.09.2022
 */
class ExtrapolationHelper(
    delay : Supplier<Long>,
    threadded : Supplier<Boolean>,
    private val ticks : Supplier<Int>,
    private val outOfBlocks : Supplier<Boolean>,
    private val shrink : Supplier<Boolean>
) {
    constructor(
        threads : MultiThreaddableModulePattern,
        ticks : Supplier<Int>,
        outOfBlocks : Supplier<Boolean>,
        shrink : Supplier<Boolean>
    ) : this(
        threads.delay.supplierLong,
        threads.multiThread.supplierBoolean,
        ticks,
        outOfBlocks,
        shrink
    )

    private var handler = ThreadHandler(delay, threadded)

    fun reset() {
        handler.reset()
    }

    fun update() {
        handler.update(Runnable {
            for(player in mc.world.playerEntities) {
                var predictor = predictor(player)

                if(player.isDead && predictor != null) {
                    predictor.active = false
                    continue
                }

                if(predictor == null && ticks.get() != 0)  {
                    predictor = MotionPredictor(mc.world, player)
                    (player as IEntityPlayer).predictor = predictor
                }

                updatePredictor(
                    predictor!!,
                    ticks.get()
                )
            }
        })
    }

    private fun updatePredictor(
        predictor : MotionPredictor,
        ticks : Int
    ) {
        predictor.active = false

        predictor.outOfBlocks = outOfBlocks.get()
        predictor.shrink = shrink.get()
        predictor.copyLocationAndAnglesFrom(predictor.player)
        predictor.detectWasPhasing()

        for(i in 1..ticks) {
            predictor.updateFromTrackedEntity()
        }

        predictor.active = true
    }

    fun predictor(
        player : EntityPlayer
    ) : MotionPredictor? = (player as IEntityPlayer).predictor
}