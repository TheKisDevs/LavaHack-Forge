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
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.init.Blocks
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
    val entities = register(SettingGroup(Setting("Entities", this))) as SettingGroup
    private val entityStep = register(entities.add(Setting("Entity Step", this, false)))
    private val entityStepVal = register(entities.add(Setting("Entity Step Value", this, 1.0, 1.0, 4.0, true).setVisible { entityStep.valBoolean }))

    private val blocks = register(SettingGroup(Setting("Blocks", this)))
    private val step = register(blocks.add(Setting("Step", this, false)))
    private val stepVal = register(blocks.add(Setting("Step Value", this, 2.0, 1.0, 4.0, true).setVisible { step.valBoolean }))
    val reverseStep : Setting = register(blocks.add(Setting("Reverse Step", this, false)))
    private val reverseStepVal = register(blocks.add(Setting("Reverse Step Value", this, 2.0, 1.0, 4.0, true).setVisible { reverseStep.valBoolean }))
    private val reverseStepLagTime = register(blocks.add(Setting("Reverse Step Lag Time", this, false).setVisible { reverseStep.valBoolean }))
    private val reverseStepLagTimeVal = register(blocks.add(Setting("Reverse Step Lag Time Value", this, 500.0, 0.0, 2000.0, NumberType.TIME).setVisible { reverseStep.valBoolean && reverseStepLagTime.valBoolean }))
    private val parkour = register(blocks.add(Setting("Parkour", this, false)))

    private val move = register(SettingGroup(Setting("Move", this)))
    val sprint : Setting = register(move.add(Setting("Sprint", this, SprintModes.None)))
    private val sprintOnlyWhileMoving = register(move.add(Setting("Sprint Only While Moving", this, false).setVisible { sprint.valEnum != SprintModes.None }))
    private val autoWalk = register(move.add(Setting("Auto Walk", this, false)))
    private val autoJump = register(move.add(Setting("Auto Jump", this, false)))
    private val autoSneak = register(move.add(Setting("Auto Sneak", this, false)))
    private val iceSpeed = register(move.add(Setting("Ice Speed", this, false)))
    private val iceSpeedVal = register(move.add(Setting("Ice Speed Val", this, 0.4, 0.2, 1.5, false).setVisible { iceSpeed.valBoolean }))
    private val fastSwim = register(move.add(Setting("Fast Swim", this, false)))
    private val fastLadder = register(move.add(Setting("Fast Ladder", this, false)))
    private val entityControl = register(move.add(Setting("Entity Control", this, false)))

    private val delays = register(SettingGroup(Setting("Delays", this)))

    private val noJumpDelay = register(delays.add(Setting("No Jump Delay", this, false)))
    private val noStepDelay = register(delays.add(Setting("No Step Delay", this, false)))

    private val lagTimer = TimerUtils()

    init {
        step.setDisplayInfo { "[${stepVal.valInt.toString()}]" }
        reverseStep.setDisplayInfo { "[${reverseStepVal.valInt.toString()}]" }
        entityStep.setDisplayInfo { "[${entityStepVal.valInt.toString()}]" }
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
    }

    override fun update() {
        if(mc.player == null || mc.world == null) return

        mc.player.stepHeight = (if(step.valBoolean) stepVal.valFloat else 0.5f)

        if(mc.player.ridingEntity != null) {
            mc.player.ridingEntity.stepHeight = (if(entityStep.valBoolean) entityStepVal.valFloat else 0.5f)
        }

        doReverseStep()
        doAutoMoving()
        doSprint()
        doDelays()
        doIceSpeed()
        doFastSwim()
        doParkour()
        doFastLadder()
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
            ++mc.player.movementInput.moveForward
            mc.player.movementInput.forwardKeyDown = true
//            mc.gameSettings.keyBindJump.pressed = true
        }
        if(autoWalk.valBoolean) {
            mc.gameSettings.keyBindForward.pressed = true
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
}