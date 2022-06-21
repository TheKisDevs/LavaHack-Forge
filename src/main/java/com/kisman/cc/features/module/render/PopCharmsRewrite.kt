package com.kisman.cc.features.module.render

import com.kisman.cc.event.events.subscribe.TotemPopEvent
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.mojang.authlib.GameProfile
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * @author _kisman_
 * @since 11:00 of 21.06.2022
 */
class PopCharmsRewrite : Module(
    "PopCharmsRewrite",
    "Like Charms but for pops or idk xD",
    Category.RENDER
) {
    private val self = register(Setting("Self", this, false))

    private val pops = ArrayList<Pop>()

    @SubscribeEvent fun onRenderWorld(event : RenderWorldLastEvent) {

    }

    @SubscribeEvent fun onPop(event : TotemPopEvent) {
        if(event.popEntity is EntityPlayer) {
            if(event.popEntity == mc.player && !self.valBoolean) {
                return
            }

            pops.add(Pop(event.popEntity as EntityPlayer))
        }
    }

    private class Pop(entity : EntityPlayer) {
        val model : EntityOtherPlayerMP
        val timestamp = System.currentTimeMillis()

        init {
            val profile = GameProfile(entity.uniqueID, "")
            model = EntityOtherPlayerMP(mc.world, profile)
            model.copyLocationAndAnglesFrom(entity)
            model.rotationYaw = entity.rotationYaw
            model.rotationYawHead = entity.rotationYawHead
            model.rotationPitch = entity.rotationPitch
            model.prevRotationYaw = entity.prevRotationYaw
            model.prevRotationPitch = entity.prevRotationPitch
            model.renderYawOffset = entity.renderYawOffset
            model.moveForward = entity.moveForward
            model.moveStrafing = entity.moveStrafing
            model.swingingHand = entity.swingingHand
        }
    }
}