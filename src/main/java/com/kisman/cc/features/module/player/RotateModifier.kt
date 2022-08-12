package com.kisman.cc.features.module.player

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.TurnEvent
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.util.math.MathHelper
import net.minecraftforge.client.event.EntityViewRenderEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * @author _kisman_
 * @since 11:20 of 12.08.2022
 */
class RotateModifier : Module(
    "RotateModifier",
    "Extra features of rotation system, like no pitch limit or free look",
    Category.PLAYER
) {
    private val freeLook = register(Setting("Free Look", this, false))

    private val rotationLockGroup = register(SettingGroup(Setting("Rotation Lock", this)))

    private val rotationLockLogic = register(rotationLockGroup.add(Setting("Rotation Lock Logic", this, RotationLockLogic.None).setTitle("Logic")))
    private val rotationLockYaw = register(rotationLockGroup.add(Setting("Rotation Lock Yaw", this, false).setTitle("Yaw")))
    private val rotationLockYawValue = register(rotationLockGroup.add(Setting("Rotation Lock Yaw Value", this, 0.0, -180.0, 180.0, true).setVisible(rotationLockYaw).setTitle("Yaw")))
    private val rotationLockPitch = register(rotationLockGroup.add(Setting("Rotation Lock Pitch", this, false).setTitle("Pitch")))
    private val rotationLockPitchValue = register(rotationLockGroup.add(Setting("Rotation Lock Pitch Value", this, 0.0, -90.0, 90.0, true).setVisible(rotationLockPitch).setTitle("Pitch")))

    private val noPitchLimit = register(Setting("No Pitch Limit", this, false))

    private var dYaw = 0f
    private var dPitch = 0f

    override fun onEnable() {
        super.onEnable()
        Kisman.EVENT_BUS.subscribe(listener)
        dPitch = 0f
        dYaw = 0f
    }

    override fun onDisable() {
        super.onDisable()
        Kisman.EVENT_BUS.unsubscribe(listener)
    }

    override fun update() {
        if(mc.player == null || mc.world == null) {
            return
        }

        doRotationLock()
    }

    private fun doRotationLock() {
        if(rotationLockLogic.valEnum != RotationLockLogic.None) {
            if(rotationLockYaw.valBoolean) {
                mc.player.rotationYaw = rotationLockYawValue.valFloat
            }
            if(rotationLockPitch.valBoolean) {
                mc.player.rotationPitch = rotationLockPitchValue.valFloat
            }
        }
    }

    private fun isFreeLookActive() : Boolean = freeLook.valBoolean || rotationLockLogic.valEnum == RotationLockLogic.FreeLook


    @SubscribeEvent
    fun onCameraSetup(event: EntityViewRenderEvent.CameraSetup) {
        if (isFreeLookActive()) {
            event.yaw = event.yaw + dYaw
            event.pitch = event.pitch + dPitch
        }
    }

    @EventHandler
    private val listener = Listener<TurnEvent>(EventHook {
        if (isFreeLookActive()) {
            dYaw = (dYaw.toDouble() + it.yaw.toDouble() * 0.15).toFloat()
            dPitch = (dPitch.toDouble() - it.pitch.toDouble() * 0.15).toFloat()
            if(!noPitchLimit.valBoolean) {
                dPitch = MathHelper.clamp(dPitch, -90.0f, 90.0f)
            }

            it.cancel()
        } else if(noPitchLimit.valBoolean) {
            val deltaYaw = it.rotationPitch
            val deltaPitch = it.rotationYaw
            it.rotationYaw = (it.rotationYaw + it.yaw * 0.15f)
            it.rotationPitch = (it.rotationPitch - it.pitch * 0.15f)
            it.prevYaw += it.rotationYaw - deltaYaw
            it.prevPitch += it.rotationPitch - deltaPitch

            it.cancel()
        }
    })

    private enum class RotationLockLogic {
        None, Normal, FreeLook
    }
}