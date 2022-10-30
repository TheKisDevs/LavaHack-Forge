package com.kisman.cc.features.module.movement

import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.exploit.PacketFly
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.util.TimerUtils
import net.minecraft.network.play.client.CPacketPlayer

/**
 * @author _kisman_ and Cubic
 * @since 15:24 of 29.10.2022
 */
class AutoPacketFly : Module(
    "AutoPacketFly",
    "fly for crystalpvp.cc",
    Category.MOVEMENT
) {
    private val flyTime = register(Setting("Fly Time", this, 2000.0, 500.0, 10000.0, NumberType.TIME))
    private val takeoffDelay = register(Setting("Takeoff Delay", this, 1000.0, 500.0, 10000.0, NumberType.TIME))
    private val ground = register(Setting("Ground", this, true))
    private val groundPacket = register(Setting("GroundPacket", this, true))
    private val iterationsLimit = register(Setting("Iterations Limit", this, 0.0, 0.0, 10.0, true))

    private val flyTimer = TimerUtils()
    private val takeoffTimer = TimerUtils()

    private var stage = Stage.Fly
    private var iterations = 0

    override fun onEnable() {
        super.onEnable()

        iterations = 0

        if(mc.player == null || mc.world == null) {
            return
        }

        flyTimer.reset()
        stage = Stage.Prepare
    }

    override fun onDisable() {
        super.onDisable()
        if(PacketFly.instance.isToggled) {
            PacketFly.instance.toggle()
        }

        if(mc.player == null || mc.world == null) {
            return
        }

        mc.gameSettings.keyBindForward.pressed = false
    }

    override fun update() {
        if(mc.player == null || mc.world == null) {
            return
        }

        if(stage == Stage.Prepare) {
            if(mc.player.onGround) {
                mc.player.jump()
            }

            mc.gameSettings.keyBindForward.pressed = true

            if(!PacketFly.instance.isToggled) {
                PacketFly.instance.toggle()
            }

            stage = Stage.Fly
        } else if(stage == Stage.Fly) {
            mc.gameSettings.keyBindForward.pressed = true

            if(flyTimer.passedMillis(flyTime.valLong)) {
                takeoffTimer.reset()
                stage = Stage.Takeoff
            }
        } else if(stage == Stage.Takeoff) {
            iterations++

            if(iterations >= iterationsLimit.valInt) {
                toggle()
                return
            }

            if(PacketFly.instance.isToggled) {
                PacketFly.instance.toggle()
            }

            if(groundPacket.valBoolean) {
                mc.player.connection.sendPacket(CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true))
            }

            if(ground.valBoolean) {
                mc.player.onGround = true
            }

            mc.gameSettings.keyBindForward.pressed = false

            if(takeoffTimer.passedMillis(takeoffDelay.valLong)) {
                flyTimer.reset()
                stage = Stage.Prepare
            }
        }
    }

    private enum class Stage {
        Prepare,
        Fly,
        Takeoff
    }
}