package com.kisman.cc.features.aiimprovements

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.client.loadingscreen.progressbar.EventProgressBar
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.ai.EntityAILookIdle
import net.minecraft.entity.ai.EntityAITasks
import net.minecraft.entity.ai.EntityAIWatchClosest
import net.minecraft.entity.ai.EntityLookHelper
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * @author _kisman_
 * @since 18:49 of 11.01.2023
 */
object AIImprovementsMod {
    @JvmField var STATE = false
    @JvmField var REMOVE_LOOK_AI = false
    @JvmField var REMOVE_LOOK_IDLE = false
    @JvmField var REPLACE_LOOK_HELPER = false

    @JvmStatic fun preInit() {
        Kisman.instance.progressBar.steps++
    }

    @JvmStatic fun init() {
        Kisman.EVENT_BUS.post(EventProgressBar("AI Improvements"))
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent fun entityJoinWorld(
        event : EntityJoinWorldEvent
    ) {
        if(!STATE) {
            return
        }

        val entity = event.entity

        if(entity is EntityLiving) {
            if(REMOVE_LOOK_AI || REMOVE_LOOK_IDLE) {
                val iterator = entity.tasks.taskEntries.iterator()

                while(iterator.hasNext()) {
                    val next = iterator.next()

                    if(next is EntityAITasks.EntityAITaskEntry) {
                        if(!(REMOVE_LOOK_AI && next.action is EntityAIWatchClosest) && (!REMOVE_LOOK_IDLE || next.action is EntityAILookIdle)) {
                            continue
                        }

                        iterator.remove()
                    }
                }
            }

            if(REPLACE_LOOK_HELPER && (entity.lookHelper == null || entity.lookHelper::class.java == EntityLookHelper::class.java)) {
                val old = entity.lookHelper

                entity.lookHelper = ImprovedLookHelper(entity)
                entity.lookHelper.posX = old.posX
                entity.lookHelper.posY = old.posY
                entity.lookHelper.posZ = old.posZ
                entity.lookHelper.isLooking = old.isLooking
                entity.lookHelper.deltaLookYaw = old.deltaLookYaw
                entity.lookHelper.deltaLookPitch = old.deltaLookPitch
            }
        }
    }
}