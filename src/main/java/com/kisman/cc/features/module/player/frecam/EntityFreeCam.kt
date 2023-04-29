package com.kisman.cc.features.module.player.frecam

import com.kisman.cc.settings.Setting
import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.entity.EntityCopied
import com.kisman.cc.util.movement.MovementUtil
import com.mojang.authlib.GameProfile
import net.minecraft.entity.MoverType
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

/**
 * @author _kisman_
 * @since 19:51 of 18.04.2023
 */
class EntityFreeCam(
    world : World,
    profile : GameProfile,
    player : EntityPlayer,
    private val id : Int,
    private val speedH : Setting,
    private val speedV : Setting
) : EntityCopied(
    world,
    profile,
    player
) {
    init {
        sync()

        mc.addScheduledTask {
            mc.world.addEntityToWorld(id, this)
        }
    }

    override fun onUpdate() {
        super.onUpdate()

        val motions = MovementUtil.strafe(MovementUtil.DEFAULT_SPEED)
        var motionY = 0.0

        if(mc.gameSettings.keyBindJump.pressed) {
            motionY += MovementUtil.DEFAULT_SPEED * speedV.valDouble
        }

        if(mc.gameSettings.keyBindSneak.pressed) {
            motionY -= MovementUtil.DEFAULT_SPEED * speedV.valDouble
        }

        move(MoverType.SELF, speedH.valDouble * motions[0], motionY, speedH.valDouble * motions[1])
    }
}