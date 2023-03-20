package com.kisman.cc.features.module.movement.fly

import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.ModuleInfo
import com.kisman.cc.settings.types.SettingEnum
import net.minecraft.network.play.client.CPacketEntityAction

/**
 * @author _kisman_
 * @since 10:55 of 19.03.2023
 */
@ModuleInfo(
    name = "Fly",
    display = "Normal",
    submodule = true
)
class NormalFly : Module() {
    private val mode = register(SettingEnum<Mode>("Mode", this, Mode.Vanilla))

    init {
        setDisplayInfo { "[${mode.valEnum}]" }
    }

    override fun onDisable() {
        if(mc.player == null || mc.world == null) {
            return
        }

        mc.player.capabilities.isFlying = false
        mc.player.capabilities.flySpeed = 0.1f
    }

    override fun update() {
        if(mc.player == null || mc.world == null) {
            return
        }

        when(mode.valEnum!!) {
            Mode.Vanilla -> {
                mc.player.capabilities.isFlying = true
                mc.player.capabilities.flySpeed = 0.05f
            }

            Mode.WellMode -> {
                if(mc.player.onGround) {
                    mc.player.motionY = 1.0
                } else {
                    mc.player.capabilities.isFlying = true
                    mc.player.capabilities.flySpeed = 1.3f
                    mc.player.motionX = 0.0
                    mc.player.motionY = -0.02
                    mc.player.motionZ = 0.0
                }
            }

            Mode.ReallyWorld -> {
                if(mc.gameSettings.keyBindJump.pressed) {
                    if(mc.player.ticksExisted % 3 == 0) {
                        mc.player.connection.sendPacket(CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING))
                    }

                    mc.player.jump()
                }
            }
        }
    }

    enum class Mode {
        Vanilla,
        WellMode,
        ReallyWorld
    }
}