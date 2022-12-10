package com.kisman.cc.features.module.movement

import com.kisman.cc.Kisman
import com.kisman.cc.event.events.EventEntityControl
import com.kisman.cc.event.events.PacketEvent
import com.kisman.cc.features.module.Category
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.settings.types.number.NumberType
import com.kisman.cc.util.movement.MovementUtil
import com.kisman.cc.util.TimerUtils
import com.kisman.cc.util.enums.SprintModes
import com.kisman.cc.util.movement.active
import com.kisman.cc.util.movement.gotoXZInverted
import com.kisman.cc.util.movement.stop
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.init.Blocks
import net.minecraft.init.MobEffects
import net.minecraft.network.play.client.CPacketPlayer

/**
 * @author _kisman_
 * @since 12.05.2022
 */
class MoveModifier : Module(
        "MoveModifier",
        "Extra movement features.",
        Category.MOVEMENT
) {
    private val entities = register(SettingGroup(Setting("Entities", this)))
    private val entityStepGroup = register(entities.add(SettingGroup(Setting("Step", this))))
    private val entityStep = register(entityStepGroup.add(Setting("Entity Step", this, false).setTitle("Step")))
    private val entityStepVal = register(entityStepGroup.add(Setting("Entity Step Value", this, 1.0, 1.0, 4.0, true).setVisible(entityStep).setTitle("Height")))
    private val entitySpeedGroup = register(entities.add(SettingGroup(Setting("Speed", this))))
    private val entitySpeed = register(entitySpeedGroup.add(Setting("Entity Speed", this, false).setTitle("Speed")))
    private val entitySpeedVal = register(entitySpeedGroup.add(Setting("Entity Speed Value", this, 1.0, 1.0, 10.0, true).setVisible(entitySpeed).setTitle("Speed")))

    private val blocks = register(SettingGroup(Setting("Blocks", this)))
    private val stepGroup = register(blocks.add(SettingGroup(Setting("Step", this))))
    private val step = register(stepGroup.add(Setting("Step", this, false)))
    val stepVal = register(stepGroup.add(Setting("Step Value", this, 2.0, 1.0, 4.0, true).setVisible(step).setTitle("Height")))
    private val reverseStepGroup = register(blocks.add(SettingGroup(Setting("Reverse Step", this))))
    val reverseStep : Setting = register(reverseStepGroup.add(Setting("Reverse Step", this, false).setTitle("RStep")))
    private val reverseStepVal = register(reverseStepGroup.add(Setting("Reverse Step Value", this, 2.0, 1.0, 4.0, true).setVisible(reverseStep).setTitle("Height")))
    private val reverseAntiGlitch = register(reverseStepGroup.add(Setting("Reverse Anti Glitch", this, true).setVisible(reverseStep).setTitle("AntiGlitch")))
    private val reverseStepLagTimeGroup = register(reverseStepGroup.add(SettingGroup(Setting("Lag Time", this))))
    private val reverseStepLagTime = register(reverseStepLagTimeGroup.add(Setting("Reverse Step Lag Time", this, false).setTitle("State")))
    private val reverseStepLagTimeVal = register(reverseStepLagTimeGroup.add(Setting("Reverse Step Lag Time Value", this, 500.0, 0.0, 2000.0, NumberType.TIME).setTitle("Value")))
    private val parkour = register(blocks.add(Setting("Parkour", this, false)))

    private val move = register(SettingGroup(Setting("Move", this)))
    private val sprintGroup = register(move.add(SettingGroup(Setting("Sprint", this))))
    val sprint : Setting = register(sprintGroup.add(Setting("Sprint", this, SprintModes.None)))
    private val sprintOnlyWhileMoving = register(sprintGroup.add(Setting("Sprint Only While Moving", this, false).setVisible { sprint.valEnum != SprintModes.None }.setTitle("While Move")))
    val keepSprint : Setting = register(move.add(Setting("Keep Sprint", this, false)))
    private val autoWalk = register(move.add(Setting("Auto Walk", this, AutoWalkMode.None)))
    private val autoJump = register(move.add(Setting("Auto Jump", this, false)))
    private val autoSneak = register(move.add(Setting("Auto Sneak", this, false)))
    private val iceSpeedGroup = register(move.add(SettingGroup(Setting("Ice Speed", this))))
    private val iceSpeed = register(iceSpeedGroup.add(Setting("Ice Speed", this, false)))
    private val iceSpeedVal = register(iceSpeedGroup.add(Setting("Ice Speed Val", this, 0.4, 0.2, 1.5, false).setVisible(iceSpeed).setTitle("Speed")))
    private val fastSwim = register(move.add(Setting("Fast Swim", this, false)))
    private val fastLadder = register(move.add(Setting("Fast Ladder", this, false)))
    private val controls = register(move.add(SettingGroup(Setting("Controls", this))))
    private val entityControl = register(controls.add(Setting("Entity Control", this, false).setTitle("Entity")))
    private val levitationControlGroup = register(controls.add(SettingGroup(Setting("Levitation", this))))
    private val levitationControl = register(levitationControlGroup.add(Setting("Levitation Control", this, false).setTitle("Levitation")))
    private val levitationControlUpSpeed = register(levitationControlGroup.add(Setting("Levitation Control Up Speed", this, 1.0, 0.0, 2.0, false).setVisible(levitationControl).setTitle("Up")))
    private val levitationControlDownSpeed = register(levitationControlGroup.add(Setting("Levitation Control Down Speed", this, 1.0, 0.0, 2.0, false).setVisible(levitationControl).setTitle("Down")))
    private val instantGroup = register(move.add(SettingGroup(Setting("Instant", this))))
    private val instant = register(instantGroup.add(Setting("Instant State", this, false).setTitle("State")))
    private val instantLiquids = register(instantGroup.add(Setting("Instant Liquids", this, false).setTitle("Liquids")))
    private val instantSlow = register(instantGroup.add(Setting("Instant Slow", this, false).setTitle("Slow")))
    private val motionLimiter = register(move.add(SettingGroup(Setting("Motion Limiter", this))))
    private val motionLimiterX = register(motionLimiter.add(SettingGroup(Setting("X", this))))
    private val motionLimiterPositiveXState = register(motionLimiterX.add(Setting("Motion Limiter Positive X State", this, false).setTitle("\"+\" State")))
    private val motionLimiterPositiveXValue = register(motionLimiterX.add(Setting("Motion Limiter Positive X Value", this, 1.0, 0.0, 5.0, false).setTitle("\"+\" Value")))
    private val motionLimiterNegativeXState = register(motionLimiterX.add(Setting("Motion Limiter Negative X State", this, false).setTitle("\"-\" State")))
    private val motionLimiterNegativeXValue = register(motionLimiterX.add(Setting("Motion Limiter Negative X Value", this, 1.0, 0.0, 5.0, false).setTitle("\"-\" Value")))
    private val motionLimiterY = register(motionLimiter.add(SettingGroup(Setting("Y", this))))
    private val motionLimiterPositiveYState = register(motionLimiterY.add(Setting("Motion Limiter Positive Y State", this, false).setTitle("\"+\" State")))
    private val motionLimiterPositiveYValue = register(motionLimiterY.add(Setting("Motion Limiter Positive Y Value", this, 1.0, 0.0, 5.0, false).setTitle("\"+\" Value")))
    private val motionLimiterNegativeYMode = register(motionLimiterY.add(Setting("Motion Limiter Negative Y Mode", this, MotionLimiterNegativeYMode.None).setTitle("\"-\" Mode")))
    private val motionLimiterNegativeYValue = register(motionLimiterY.add(Setting("Motion Limiter Negative Y Value", this, 1.0, 0.0, 5.0, false).setTitle("\"-\" Value")))
    private val motionLimiterZ = register(motionLimiter.add(SettingGroup(Setting("Z", this))))
    private val motionLimiterPositiveZState = register(motionLimiterZ.add(Setting("Motion Limiter Positive Z State", this, false).setTitle("\"+\" State")))
    private val motionLimiterPositiveZValue = register(motionLimiterZ.add(Setting("Motion Limiter Positive Z Value", this, 1.0, 0.0, 5.0, false).setTitle("\"+\" Value")))
    private val motionLimiterNegativeZState = register(motionLimiterZ.add(Setting("Motion Limiter Negative Z State", this, false).setTitle("\"-\" State")))
    private val motionLimiterNegativeZValue = register(motionLimiterZ.add(Setting("Motion Limiter Negative Z Value", this, 1.0, 0.0, 5.0, false).setTitle("\"-\" Value")))


    private val delays = register(SettingGroup(Setting("Delays", this)))

    private val noJumpDelay = register(delays.add(Setting("No Jump Delay", this, false)))
    private val noStepDelay = register(delays.add(Setting("No Step Delay", this, false)))

    private val lagTimer = TimerUtils()

    companion object {
        @JvmStatic var instance : MoveModifier? = null
    }

    init {
        step.setDisplayInfo { "[${stepVal.valInt}]" }
        reverseStep.setDisplayInfo { "[${reverseStepVal.valInt}]" }
        entityStep.setDisplayInfo { "[${entityStepVal.valInt}]" }
        entitySpeed.setDisplayInfo { "[${entitySpeedVal.valInt}]" }
        sprint.setDisplayInfo { "[${sprint.valEnum}]" }
        iceSpeed.setDisplayInfo { "[${iceSpeedVal.valInt}]" }

        instance = this
    }

    override fun onEnable() {
        Kisman.EVENT_BUS.subscribe(send)
        Kisman.EVENT_BUS.subscribe(entityControlListener)
        lagTimer.reset()
    }

    override fun onDisable() {
        Kisman.EVENT_BUS.unsubscribe(entityControlListener)
        Kisman.EVENT_BUS.unsubscribe(send)

        if(mc.player == null || mc.world == null) return

        mc.player.stepHeight = 0.5f
        mc.player.isSprinting = false

        if(mc.player.ridingEntity != null) {
            mc.player.ridingEntity.stepHeight = 0.5f
        }

        onDisableIceSpeed()

        if(active()) {
            stop()
        }
    }

    override fun update() {
        if(mc.player == null || mc.world == null || mc.playerController == null) return

        mc.player.stepHeight = (if(step.valBoolean) stepVal.valFloat else 0.5f)

        if(mc.player.ridingEntity != null) {
            mc.player.ridingEntity.stepHeight = (if(entityStep.valBoolean) entityStepVal.valFloat else 0.5f)

            if(entitySpeed.valBoolean) {
                val dir = MovementUtil.forward(entitySpeedVal.valDouble)
                mc.player.ridingEntity.motionX = dir[0]
                mc.player.ridingEntity.motionZ = dir[1]
            }
        }

        doReverseStep()
        doAutoMoving()
        doSprint()
        doDelays()
        doIceSpeed()
        doFastSwim()
        doParkour()
        doFastLadder()
        doLevitationControl()
        doInstant()
        doMotionLimiter()
    }

    private fun doMotionLimiter() {
        fun limitMotion(
            currentMotion : Double,
            positiveLimit : Double,
            negativeLimit : Double,
            positiveFlag : Boolean,
            negativeFlag : Boolean
        ) : Double {
            if(currentMotion > positiveLimit && positiveFlag) {
                return positiveLimit
            }

            if(currentMotion < -negativeLimit && negativeFlag) {
                return -negativeLimit
            }

            return currentMotion
        }

        mc.player.motionX = limitMotion(mc.player.motionX, motionLimiterPositiveXValue.valDouble, motionLimiterNegativeXValue.valDouble, motionLimiterPositiveXState.valBoolean, motionLimiterNegativeXState.valBoolean)
        mc.player.motionY = limitMotion(mc.player.motionY, motionLimiterPositiveYValue.valDouble, motionLimiterNegativeYValue.valDouble, motionLimiterPositiveYState.valBoolean, motionLimiterNegativeYMode.valEnum != MotionLimiterNegativeYMode.None)
        mc.player.motionZ = limitMotion(mc.player.motionZ, motionLimiterPositiveZValue.valDouble, motionLimiterNegativeZValue.valDouble, motionLimiterPositiveZState.valBoolean, motionLimiterNegativeZState.valBoolean)

        if(mc.player.onGround && motionLimiterNegativeYMode.valEnum == MotionLimiterNegativeYMode.ReverseStep && reverseStep.valBoolean) {
            var motionY = 0.0

            var y = 0.0
            while (y < reverseStepVal.valInt + 0.5) {
                if (mc.world.getCollisionBoxes(mc.player, mc.player.entityBoundingBox.offset(0.0, -y, 0.0)).isNotEmpty()) {
                    motionY = -10.0
                    break
                }
                y += 0.01
            }

            if(mc.player.motionY < motionY) {
                mc.player.motionY = motionY
            }
        }
    }

    private fun doInstant() {
        if(instant.valBoolean && ((!mc.player.isInWater && !mc.player.isInLava) || instantLiquids.valBoolean) && !mc.player.isElytraFlying) {
            val motions =MovementUtil.strafe(MovementUtil.getSpeed(
                instantSlow.valBoolean, MovementUtil.DEFAULT_SPEED
            ))

            mc.player.motionX = motions[0]
            mc.player.motionZ = motions[1]
        }
    }

    private fun doLevitationControl() {
        if(levitationControl.valBoolean && mc.player.getActivePotionEffect(MobEffects.LEVITATION) != null) {
            var flag = true

            if(mc.gameSettings.keyBindJump.isPressed) {
                mc.player.motionY = levitationControlUpSpeed.valDouble
                flag = false
            }

            if(mc.gameSettings.keyBindSneak.isPressed) {
                mc.player.motionY = levitationControlDownSpeed.valDouble
                flag = false
            }

            if(flag) {
                mc.player.motionY = 0.0
            }
        }
    }

    private fun doFastLadder() {
        if(mc.player.isOnLadder && fastLadder.valBoolean) {
            mc.player.jump()
        }
    }

    private fun doParkour() {
        if(
            parkour.valBoolean
            && mc.player.onGround
            && !mc.player.isSneaking
            && !mc.gameSettings.keyBindJump.pressed
            && mc.world.getCollisionBoxes(
                mc.player,
                mc.player.entityBoundingBox
                    .offset(0.0, -0.5, 0.0)
                    .expand(-0.0000000000000000000000000001, 0.0, -0.00000000000000000000000000001)
            ).isEmpty()
        ) {
            mc.player.jump()
        }
    }

    private fun doFastSwim() {
        if((mc.player.isInLava || mc.player.isInWater) && MovementUtil.isMoving() && fastSwim.valBoolean) {
            mc.player.isSprinting = true
            if(mc.gameSettings.keyBindJump.isKeyDown) {
                mc.player.motionY = 0.098
            }
        }
    }

    private fun doIceSpeed() {
        if(iceSpeed.valBoolean) {
            Blocks.ICE.slipperiness = iceSpeedVal.valFloat
            Blocks.PACKED_ICE.slipperiness = iceSpeedVal.valFloat
            Blocks.FROSTED_ICE.slipperiness = iceSpeedVal.valFloat
        }
    }

    private fun onDisableIceSpeed() {
        Blocks.ICE.slipperiness = 0.98f
        Blocks.PACKED_ICE.slipperiness = 0.98f
        Blocks.FROSTED_ICE.slipperiness = 0.98f
    }

    private fun doDelays() {
        if(noJumpDelay.valBoolean) {
            mc.player.jumpTicks = 0
            mc.player.nextStepDistance = 0
        }

        if(noStepDelay.valBoolean) {
            mc.playerController.stepSoundTickCounter = 0f
        }
    }

    private fun doSprint() {
        if(sprint.valEnum != SprintModes.None) {
            if(sprintOnlyWhileMoving.valBoolean && !MovementUtil.isMoving()) return
            if(sprint.valEnum == SprintModes.Legit && !mc.gameSettings.keyBindForward.pressed) return
            mc.player.isSprinting = true
        }
    }

    private fun doAutoMoving() {
        if(autoJump.valBoolean) {
            mc.gameSettings.keyBindJump.pressed = true
        }
        if(autoWalk.valEnum != AutoWalkMode.None) {
            if(autoWalk.valEnum == AutoWalkMode.Stupid) {
                mc.gameSettings.keyBindForward.pressed = true
                if(active()) {
                    stop()
                }
            } else if(!active()) {
                gotoXZInverted(0, 0)
            }
        } else {
            if(active()) {
                stop()
            }
        }
        if(autoSneak.valBoolean) {
            mc.gameSettings.keyBindSneak.pressed = true
        }
    }

    private fun doReverseStep() {
        if (reverseStep.valBoolean && mc.player.onGround && !mc.player.isInWater && !mc.player.isOnLadder) {
            if (lagTimer.passedMillis(if (reverseStepLagTime.valBoolean) reverseStepLagTimeVal.valLong else 500L)) {
                lagTimer.reset()
                if (reverseStepLagTime.valBoolean) return
            }
            var y = 0.0
            while (y < reverseStepVal.valInt + 0.5) {
                if(reverseAntiGlitch.valBoolean && y < 0.5){
                    y += 0.01
                    continue
                }
                if (mc.world.getCollisionBoxes(mc.player, mc.player.entityBoundingBox.offset(0.0, -y, 0.0)).isNotEmpty()) {
                    mc.player.motionY = -10.0
                    break
                }
                y += 0.01
            }
        }
    }

    private val send = Listener<PacketEvent.Send>(EventHook {
        if(mc.player != null && mc.player.isOnLadder && fastLadder.valBoolean && MovementUtil.isMoving() && it.packet is CPacketPlayer) {
            (it.packet as CPacketPlayer).onGround = true;
        }
    })

    private val entityControlListener = Listener<EventEntityControl>(EventHook {
        if(entityControl.valBoolean) {
            it.cancel()
        }
    })

    private enum class AutoWalkMode {None, Stupid, Smart}
    private enum class MotionLimiterNegativeYMode {None, Value, ReverseStep}
}