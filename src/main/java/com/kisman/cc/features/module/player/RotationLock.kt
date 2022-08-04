package com.kisman.cc.features.module.player

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.TurnEvent
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.util.math.MathHelper
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * @author _kisman_
 * @since 17:15 of 04.06.2022
 */
class RotationLock : Module(
    "RotationLock",
    "Locks your yaw and pitch.",
    Category.PLAYER
) {
    private val yaw = register(Setting("Yaw", this, false))
    private val yawValue = register(Setting("Yaw Value", this, 0.0, -180.0, 180.0, true).setVisible { yaw.valBoolean })
    private val pitch = register(Setting("Pitch", this, false))
    private val pitchValue = register(Setting("Pitch Value", this, 0.0, -90.0, 90.0, true).setVisible { pitch.valBoolean })
    private val freeLook = register(Setting("Free Look", this, false))

    private var dYaw = 0f
    private var dPitch = 0f

    override fun update() {
        if(mc.player == null || mc.world == null) {
            return
        }

        if(yaw.valBoolean) {
            mc.player.rotationYaw = yawValue.valFloat
        }
        if(pitch.valBoolean) {
            mc.player.rotationPitch = pitchValue.valFloat
        }
    }

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

    @SubscribeEvent
    fun onCameraSetup(event: CameraSetup) {
        if (freeLook.valBoolean) {
            event.yaw = event.yaw + dYaw
            event.pitch = event.pitch + dPitch
        }
    }

    @EventHandler
    private val listener = Listener<TurnEvent>(EventHook {
        if (freeLook.valBoolean) {
            dYaw = (dYaw.toDouble() + it.yaw.toDouble() * 0.15).toFloat()
            dPitch = (dPitch.toDouble() - it.pitch.toDouble() * 0.15).toFloat()
            dPitch = MathHelper.clamp(dPitch, -90.0f, 90.0f)
            it.cancel()
        }
    })
}