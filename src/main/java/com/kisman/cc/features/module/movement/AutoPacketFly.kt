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
    private val groundPacket = register(Setting("Ground Packet", this, true))
    private val takeoffLogic = register(Setting("Takeoff Logic", this, TakeoffLogic.PacketFlyState))
    private val takeoffFactor = register(Setting("Takeoff Factor", this, 1.0, 0.1, 10.0, false))
    private val takeoffStopMoving = register(Setting("Takeoff Stop Moving", this, true))
    private val iterationsLimit = register(Setting("Iterations Limit", this, 0.0, 0.0, 10.0, true))
    private val instantJump = register(Setting("Instant Jump", this, true))

    private val flyTimer = TimerUtils()
    private val takeoffTimer = TimerUtils()

    private var stage = Stage.Fly
    private var iterations = 0

    private var prevFactor = -1.0

    override fun onEnable() {
        super.onEnable()

        if(mc.player == null || mc.world == null) {
            toggle()
            return
        }

        iterations = 0
        prevFactor = -1.0
        flyTimer.reset()
        stage = Stage.PrepareFly
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

        if(stage == Stage.PrepareFly) {
            if(mc.player.onGround) {
                mc.player.jump()
            }

            if(prevFactor != -1.0) {
                PacketFly.instance.factor.valDouble = prevFactor
            }

            mc.gameSettings.keyBindForward.pressed = true

            if(!PacketFly.instance.isToggled) {
                PacketFly.instance.toggle()
            }

            stage = Stage.Jumping
        } else if(stage == Stage.Jumping) {
            if(instantJump.valBoolean || mc.player.motionY == 0.0) {
                stage = Stage.Fly
            }
        } else if(stage == Stage.Fly) {
            mc.gameSettings.keyBindForward.pressed = true

            if(flyTimer.passedMillis(flyTime.valLong)) {
                takeoffTimer.reset()
                stage = Stage.PrepareTakeoff
            }
        } else if(stage == Stage.PrepareTakeoff) {
            prevFactor = PacketFly.instance.factor.valDouble
            stage = Stage.Takeoff
        } else if(stage == Stage.Takeoff) {
            iterations++

            if(iterations >= iterationsLimit.valInt) {
                toggle()
                return
            }

            if(takeoffLogic.valEnum == TakeoffLogic.PacketFlyState) {
                if (PacketFly.instance.isToggled) {
                    PacketFly.instance.toggle()
                }
                prevFactor = -1.0
            } else if(takeoffLogic.valEnum == TakeoffLogic.FactorValue) {
                PacketFly.instance.factor.valDouble = takeoffFactor.valDouble
            }

            if(groundPacket.valBoolean) {
                mc.player.connection.sendPacket(CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true))
            }

            if(ground.valBoolean) {
                mc.player.onGround = true
            }

            if(takeoffStopMoving.valBoolean) {
                mc.gameSettings.keyBindForward.pressed = false
            }

            if(takeoffTimer.passedMillis(takeoffDelay.valLong)) {
                flyTimer.reset()
                stage = Stage.PrepareFly
            }
        }
    }

    private enum class Stage {
        PrepareFly,
        Jumping,
        Fly,
        PrepareTakeoff,
        Takeoff
    }

    private enum class TakeoffLogic {
        PacketFlyState,
        FactorValue
    }
}