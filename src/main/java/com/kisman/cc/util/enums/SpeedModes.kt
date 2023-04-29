package com.kisman.cc.util.enums

import com.kisman.cc.Kisman
import com.kisman.cc.event.Event
import com.kisman.cc.event.events.EventPlayerMotionUpdate
import com.kisman.cc.event.events.EventPlayerMove
import com.kisman.cc.event.events.PacketEvent
import com.kisman.cc.features.module.Module
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.TimerUtils
import com.kisman.cc.util.block
import com.kisman.cc.util.entity.EntityUtil
import com.kisman.cc.util.math.sqrt2
import com.kisman.cc.util.movement.MovementUtil
import com.kisman.cc.util.world.playerPosition
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.init.Blocks
import net.minecraft.init.MobEffects
import net.minecraft.item.ItemBook
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemPotion
import net.minecraft.network.play.client.CPacketEntityAction
import net.minecraft.network.play.server.SPacketExplosion
import net.minecraft.network.play.server.SPacketPlayerPosLook
import net.minecraft.util.math.BlockPos
import kotlin.math.max
import kotlin.math.min

/**
 * @author _kisman_
 * @since 18:24 of 20.04.2023
 */
enum class SpeedModes(
    val handler : ISpeedMode
) {
    Strafe(object : ISpeedMode {
        private val speed = register(Setting("Speed", null, 0.2873, 0.05, 1.0, false), "Strafe")
        private val potionMult = register(Setting("Potion Mult", null, 1.0, 0.1, 5.0, false), "Strafe")
        private val strict = register(Setting("Strict", null, false), "Strafe")
        private val sprint = register(Setting("Sprint", null, false), "Strafe")
        private val boost = register(Setting("Boost", null, false), "Strafe")
        private val fastFall = register(Setting("Fast Fall", null, false), "Strafe")

        private var currentSpeed = 0.0
        private var prevMotion = 0.0
        private var maxVelocity = 0.0
        private var oddStage = false
        private var stage = 4

        private val velocityTimer = TimerUtils()

        private var sneaking = false

        override fun enable() {
            Kisman.EVENT_BUS.subscribe(motion)
            Kisman.EVENT_BUS.subscribe(receive)
            maxVelocity = 0.0
            stage = 4
            currentSpeed = MovementUtil.getMoveSpeed(speed.valDouble, potionMult.valDouble)
            prevMotion = 0.0
        }

        override fun disable() {
            Kisman.EVENT_BUS.unsubscribe(motion)
            Kisman.EVENT_BUS.unsubscribe(receive)
        }

        override fun update() {
            if(mc.player == null || mc.world == null) {
                return
            }

            if(sprint.valBoolean && !mc.player.isSprinting && (mc.gameSettings.keyBindForward.pressed || mc.gameSettings.keyBindBack.pressed || mc.gameSettings.keyBindRight.pressed || mc.gameSettings.keyBindLeft.pressed)) {
                mc.player.isSprinting = true
                mc.player.connection.sendPacket(CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING))
            }

            if(strict.valBoolean) {
                if (shouldSneak()) {
                    mc.player.connection.sendPacket(CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING))
                    sneaking = true
                }

                if(shouldNotSneak()) {
                    mc.player.connection.sendPacket(CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING))
                    sneaking = false
                }
            }
        }

        override fun move(
            event : EventPlayerMove
        ) {
            event.cancel()

            if(stage != 1 || (mc.player.moveForward == 0f || mc.player.moveStrafing == 0f)) {
                if(stage == 2 && (mc.player.moveForward != 0f || mc.player.moveStrafing != 0f)) {
                    var jumpSpeed = if(mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                        (mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST)!!.amplifier + 1) * 0.1
                    } else {
                        0.0
                    }

                    if(mc.player.onGround && fastFall.valBoolean) {
                        jumpSpeed = -(1 + 0.3999)
                    }

                    mc.player.motionY = 0.3999 + jumpSpeed
                    event.y = mc.player.motionY
                    currentSpeed *= if(oddStage) {
                        1.6835
                    } else {
                        1.395
                    }
                } else if(stage == 3) {
                    val adjustedMotion = 0.66 * (prevMotion - MovementUtil.getMoveSpeed(speed.valDouble, potionMult.valDouble))

                    currentSpeed = prevMotion - adjustedMotion
                    oddStage = !oddStage
                } else {
                    val collisionBoxes = mc.world.getCollisionBoxes(mc.player, mc.player.boundingBox.offset(0.0, mc.player.motionY, 0.0))

                    if((collisionBoxes.size > 0 || mc.player.collidedVertically) && stage > 0) {
                        stage = if(mc.player.moveForward == 0f && mc.player.moveStrafing == 0f) {
                            0
                        } else {
                            1
                        }
                    }

                    currentSpeed = prevMotion - prevMotion / 159.0
                }
            } else {
                currentSpeed = 1.35 * MovementUtil.getMoveSpeed(speed.valDouble, potionMult.valDouble) - 0.01
            }

            currentSpeed = max(currentSpeed, MovementUtil.getMoveSpeed(speed.valDouble, potionMult.valDouble))

            currentSpeed = if(maxVelocity > 0 && boost.valBoolean && !velocityTimer.passedMillis(75L) && !mc.player.collidedHorizontally) {
                max(currentSpeed, maxVelocity)
            } else {
                min(currentSpeed, 0.443)
            }

            val motions = if(mc.player.movementInput.moveForward == 0f && mc.player.movementInput.moveStrafe == 0f) {
                doubleArrayOf(0.0, 0.0)
            } else {
                MovementUtil.strafe(currentSpeed)
            }

            event.x = motions[0]
            event.z = motions[1]

            if(mc.player.moveForward == 0f && mc.player.moveStrafing == 0f) {
                return
            }

            stage++
        }

        private val motion = Listener<EventPlayerMotionUpdate>(EventHook {
            if(it.era == Event.Era.POST) {
                return@EventHook
            }

            if(!(mc.gameSettings.keyBindForward.pressed || mc.gameSettings.keyBindBack.pressed || mc.gameSettings.keyBindRight.pressed || mc.gameSettings.keyBindLeft.pressed)) {
                currentSpeed = 0.0
            }

            val diffX = mc.player.posX - mc.player.prevPosX
            val diffZ = mc.player.posZ - mc.player.prevPosZ

            prevMotion = sqrt2(diffX * diffX + diffZ * diffZ)
        })

        private val receive = Listener<PacketEvent.Receive>(EventHook {
            val packet = it.packet

            if(packet is SPacketPlayerPosLook) {
                currentSpeed = 0.0
                prevMotion = 0.0
                maxVelocity = 0.0
            }

            if(packet is SPacketExplosion) {
                val motionX = packet.motionX.toDouble()
                val motionZ = packet.motionZ.toDouble()

                maxVelocity = sqrt2(motionX * motionX + motionZ * motionZ)
                velocityTimer.reset()
            }
        })

        private fun shouldSneak() : Boolean {
            val item = mc.player.activeItemStack.item
            val active = mc.player.isHandActive && (item is ItemFood || item is ItemBook || item is ItemPotion)

            return !sneaking && active
        }

        private fun shouldNotSneak() : Boolean {
            val item = mc.player.activeItemStack.item
            val active = mc.player.isHandActive && (item is ItemFood || item is ItemBook || item is ItemPotion)

            return sneaking && !active
        }
    }),

    YPort(object : ISpeedMode {
        private val speed = register(Setting("Speed", null, 1.0, 0.0, 2.0, false), "YPort")

        override fun enable() { }
        override fun disable() { }
        override fun move(
            event : EventPlayerMove
        ) { }

        override fun update() {
            if(MovementUtil.isMoving()) {
                if(mc.player.onGround) {
                    val motions = MovementUtil.strafe(MovementUtil.DEFAULT_SPEED * speed.valDouble)

                    EntityUtil.setTimer(1.15f)
                    mc.player.jump()

                    mc.player.motionX = motions[0]
                    mc.player.motionZ = motions[1]
                } else {
                    mc.player.motionY = -1.0
                    EntityUtil.resetTimer()
                }
            }
        }
    }),

    Bhop(object : ISpeedMode {
        private val useMotion = register(Setting("Use Motion", null, 4.0, 0.1, 10.0, false), "Bhop")
        private val useMotionInAir = register(Setting("Use Motion In Air", null, false), "Bhop")
        private val jumpMovementFactorSpeed = register(Setting("Jump Movement Factor Speed", null, 0.265, 0.01, 10.0, false), "Bhop")
        private val jumpMovementFactor = register(Setting("Jump Movement Factor", null, false), "Bhop")
        private val useTimer = register(Setting("Use Timer", null, false), "Bhop")

        private var lastPos : BlockPos? = null
        private var y = 0

        override fun enable() { }
        override fun disable() { }
        override fun update() { }

        override fun move(event: EventPlayerMove) {
            val motions = motion()

            mc.player.isSprinting = true

            if(mc.gameSettings.keyBindForward.pressed) {
                if(mc.player.onGround) {
                    if(useMotion.valBoolean && motions != null) {
                        mc.player.motionX += motions[0] * 0.1
                        mc.player.motionY += motions[1] * 0.1
                    }
                    y = 1

                    if(useTimer.valBoolean) {
                        EntityUtil.setTimer(1.3f)
                    }  else {
                        EntityUtil.resetTimer()
                    }

                    mc.player.jump()
                } else {
                    if(jumpMovementFactor.valBoolean) {
                        mc.player.jumpMovementFactor = jumpMovementFactorSpeed.valFloat
                    }

                    val pos = playerPosition()

                    if(y != 1) {
                        if(pos.y < y) {
                            mc.player.motionX = 0.0
                            mc.player.motionZ = 0.0

                            if(useTimer.valBoolean) {
                                EntityUtil.setTimer(16f)
                            }  else {
                                EntityUtil.resetTimer()
                            }
                        } else {
                            if(useMotionInAir.valBoolean && motions != null) {
                                mc.player.motionX += motions[0] * 0.2
                                mc.player.motionY += motions[1] * 0.2
                            }
                        }
                    }

                    y = pos.y
                }
            }
        }

        private fun motion() : Array<Int>? {
            val pos = playerPosition()

            if(block(pos) == Blocks.AIR && lastPos != null) {
                if(pos != lastPos) {
                    fun processAxis(
                        offsetX : Int,
                        offsetZ : Int,
                        current : BlockPos,
                        last : BlockPos
                    ) : Boolean = last.add(offsetX, 0, offsetZ) == current

                    processAxis(0,-1, pos, lastPos!!).also { if(it) return arrayOf(0, -1) }
                    processAxis(0,1, pos, lastPos!!).also { if(it) return arrayOf(0, 1) }
                    processAxis(1,0, pos, lastPos!!).also { if(it) return arrayOf(1, 0) }
                    processAxis(-1,0, pos, lastPos!!).also { if(it) return arrayOf(-1, 0) }
                }
            } else {
                lastPos = pos
            }

            return null
        }
    })

    ;

    val group = SettingGroup(Setting(toString(), null))

    companion object {
        fun init(
            module : Module
        ) {
            init0(module)
        }
   }
}

private val settings = mutableMapOf<String, ArrayList<Setting>>()
private val groups = mutableMapOf<String, SettingGroup>()
private var module : Module? = null

private fun register(
    setting : Setting,
    mode : String
) : Setting {
    if(!settings.contains(mode)) {
        settings[mode] = ArrayList()
    }

    if(!groups.contains(mode)) {
        groups[mode] = SettingGroup(Setting(mode, null))
    }

    settings[mode]!!.add(setting)
    groups[mode]!!.add(setting)
    setting.name = "$mode ${setting.name}"

    if(module != null) {
        println("registering ${setting.name}")

        module!!.register(setting)
    }

    return setting
}

private fun init0(
    module : Module
) {
    for(mode in SpeedModes.values()) {
        module.register(groups[mode.toString()]!!.also { it.parent = module })
    }
}

interface ISpeedMode {
    fun enable()
    fun disable()
    fun update()

    fun move(
        event : EventPlayerMove
    )
}