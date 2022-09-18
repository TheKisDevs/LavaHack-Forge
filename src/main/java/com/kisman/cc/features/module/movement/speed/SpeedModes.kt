package com.kisman.cc.features.module.movement.speed

import com.kisman.cc.features.module.Module
import com.kisman.cc.features.module.movement.Speed
import com.kisman.cc.features.module.movement.SpeedRewrite
import com.kisman.cc.features.module.movement.speed.util.Motion
import com.kisman.cc.mixin.mixins.accessor.AccessorEntityPlayer
import com.kisman.cc.util.Globals.mc
import com.kisman.cc.util.entity.EntityUtil
import com.kisman.cc.util.manager.Managers
import com.kisman.cc.util.movement.MovementUtil
import net.minecraft.init.Blocks
import net.minecraft.init.MobEffects
import net.minecraft.network.play.client.CPacketEntityAction
import net.minecraft.util.math.BlockPos
import kotlin.math.*

/**
 * @author _kisman_
 * @since 13:40 of 24.06.2022
 */
enum class SpeedModes(
    val mode : ISpeedMode
) {
    Strafe(object : ISpeedMode {
        override fun onEnable() {}
        override fun update() {
            if(MovementUtil.isMoving() && mc.player.hurtTime < 5) {
                if(mc.player.onGround) {
                    mc.player.motionY = 0.405

                    val direction = MovementUtil.getDirection()

                    mc.player.motionX -= sin(direction) * 0.2
                    mc.player.motionZ += cos(direction) * 0.2
                } else {
                    val currentSpeed = sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ)
                    val speed = if(abs(mc.player.rotationYawHead - mc.player.rotationYaw) < 90) 1.0064 else 1.001
                    val direction = MovementUtil.getDirection()

                    mc.player.motionX = -sin(direction) * speed * currentSpeed
                    mc.player.motionY = cos(direction) * speed * currentSpeed
                }
            }
        }
    }),
    YPort(object : ISpeedMode {
        override fun onEnable() {}
        override fun update() {
            if(MovementUtil.isMoving() && !mc.player.collidedHorizontally) {
                if(mc.player.onGround) {
                    EntityUtil.setTimer(1.15f)
                    mc.player.jump()
                    val direction = MovementUtil.forward(MovementUtil.getSpeed(SpeedRewrite.slow!!.valBoolean, MovementUtil.DEFAULT_SPEED))
                    mc.player.motionX = direction[0]
                    mc.player.motionZ = direction[1]
                } else {
                    mc.player.motionY = -1.0
                    EntityUtil.resetTimer()
                }
            }
        }
    }),
    StrafeNew(object : ISpeedMode {
        var stage = 4
        var speed = 0.0
        var distance = 0.0
        var boost = false

        override fun onEnable() {
            stage = 4
            distance = MovementUtil.getDistance2D()
            speed = MovementUtil.getSpeed()
        }

        override fun update() {
            if(!mc.player.isElytraFlying) {
                if(SpeedRewrite.useTimer!!.valBoolean && Managers.instance.passed(250)) {
                    EntityUtil.setTimer(1.0888f)
                }

                if(!Managers.instance.passed(SpeedRewrite.lagTime!!.valInt)) {
                    return
                }

                when(stage) {
                    1 -> {
                        if(MovementUtil.isMoving()) {
                            speed = 1.35 * MovementUtil.getSpeed(SpeedRewrite.slow!!.valBoolean, SpeedRewrite.strafeSpeed!!.valDouble) - 0.01
                        }
                    }
                    2 -> {
                        if(MovementUtil.isMoving() && mc.player.onGround) {
                            mc.player.motionY = 0.3999 + MovementUtil.getJumpSpeed()
                            speed *= if(boost) 1.6835 else 1.395
                        }
                    }
                    3 -> {
                        speed = distance - 0.66 * (distance - MovementUtil.getSpeed(SpeedRewrite.slow!!.valBoolean, SpeedRewrite.strafeSpeed!!.valDouble))
                        boost = !boost
                    }
                    else -> {
                        if((mc.world.getCollisionBoxes(null, mc.player.entityBoundingBox.offset(0.0, mc.player.motionY, 0.0)).size > 0 || mc.player.collidedHorizontally) && stage > 0) {
                            stage = if(MovementUtil.isMoving()) 1 else 0
                        }

                        speed = distance - distance / 159
                    }
                }

                speed = min(speed, getCap())
                speed = max(speed, MovementUtil.getSpeed(SpeedRewrite.slow!!.valBoolean, SpeedRewrite.strafeSpeed!!.valDouble))
                MovementUtil.strafe(speed.toFloat())

                if(MovementUtil.isMoving()) {
                    stage++
                }
            }
        }
    }),
    MatrixBhop(object : ISpeedMode {
        override fun onEnable() {}
        override fun update() {
            if(MovementUtil.isMoving()) {
                mc.gameSettings.keyBindJump.pressed = false

                if(mc.player.onGround) {
                    mc.player.jump()
                    (mc.player as AccessorEntityPlayer).setSpeedInAir(0.0208f)
                    mc.player.jumpMovementFactor = 0.1f
                    EntityUtil.setTimer(0.94f)
                }

                if(mc.player.fallDistance > 0.6 && mc.player.fallDistance < 1.3) {
                    (mc.player as AccessorEntityPlayer).setSpeedInAir(0.0208f)
                    EntityUtil.setTimer(1.8f)
                }
            }
        }
    }),
    MatrixStrafe(object : ISpeedMode {
        override fun onEnable() {}
        override fun update() {
            if(mc.player.ticksExisted % 4 == 0) {
                mc.player.connection.sendPacket(CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING))
            }

            if(MovementUtil.isMoving()) {
                if(mc.player.onGround) {
                    mc.gameSettings.keyBindJump.pressed = false
                    mc.player.jump()
                } else if(mc.player.fallDistance <= 0.1) {
                    (mc.player as AccessorEntityPlayer).setSpeedInAir(0.0202f)
                    mc.player.jumpMovementFactor = 0.027f
                    EntityUtil.setTimer(1.5f)
                } else if(mc.player.fallDistance > 0.1 && mc.player.fallDistance < 1.3) {
                    EntityUtil.setTimer(0.7f)
                } else if(mc.player.fallDistance >= 1.3) {
                    EntityUtil.resetTimer()
                    (mc.player as AccessorEntityPlayer).setSpeedInAir(0.0202f)
                    mc.player.jumpMovementFactor = 0.025f
                }
            }
        }
    }),
    Bhop(object : ISpeedMode {
        var lastPos : BlockPos? = null
        var y = 1.0

        override fun onEnable() {}

        override fun update() {
            val currentMotion = getMotion()
            mc.player.isSprinting = true

            if(mc.gameSettings.keyBindForward.isKeyDown) {
                if(mc.player.onGround) {
                    if(SpeedRewrite.useMotion!!.valBoolean && currentMotion != null) {
                        when(currentMotion) {
                            Motion.X -> mc.player.motionX += 0.1
                            Motion.Z -> mc.player.motionZ += 0.1
                            Motion.mX -> mc.player.motionX -= 0.1
                            Motion.mZ -> mc.player.motionZ -= 0.1
                        }
                    }

                    y = 1.0
                    EntityUtil.resetTimer()
                    if (SpeedRewrite.useTimer!!.valBoolean) Managers.instance.timerManager.updateTimer(Speed.instance, 2, 1.3f)
                    mc.player.jump()
                    val dirSpeed = MovementUtil.forward(MovementUtil.getSpeed(SpeedRewrite.slow!!.valBoolean, MovementUtil.DEFAULT_SPEED) * SpeedRewrite.boostSpeed!!.valDouble + (if (SpeedRewrite.boostFactor!!.valBoolean) 0.3 else 0.0))
                    mc.player.motionX = dirSpeed[0]
                    mc.player.motionZ = dirSpeed[1]
                } else {
                    if (SpeedRewrite.jumpMovementFactor!!.valBoolean) {
                        mc.player.jumpMovementFactor = SpeedRewrite.jumpMovementFactorSpeed!!.valFloat
                    }
                    if (y == 1.0) y = mc.player.positionVector.y else {
                        if (mc.player.positionVector.y < y) {
                            y = mc.player.positionVector.y
                            mc.player.motionX = 0.0
                            mc.player.motionZ = 0.0
                            if (SpeedRewrite.useTimer!!.valBoolean) {
                                EntityUtil.resetTimer()
                            }
                            Managers.instance.timerManager.updateTimer(Speed.instance, 2, 16f)
                        } else {
                            y = mc.player.positionVector.y
                            if (SpeedRewrite.useMotionInAir!!.valBoolean && currentMotion != null) {
                                when (currentMotion) {
                                    Motion.X -> mc.player.motionX += 0.2
                                    Motion.Z -> mc.player.motionY += 0.2
                                    Motion.mX -> mc.player.motionX -= 0.2
                                    Motion.mZ -> mc.player.motionY -= 0.2
                                }
                            }
                        }
                    }
                }
            }
        }

        private fun getMotion(): Motion? {
            val posToCheck = EntityUtil.getRoundedBlockPos(mc.player)
            if (mc.world.getBlockState(posToCheck) == Blocks.AIR && lastPos != null) {
                if (posToCheck != lastPos) {
                    if (lastPos?.add(0, 0, -1) == posToCheck) return Motion.mZ
                    if (lastPos?.add(0, 0, 1) == posToCheck) return Motion.Z
                    if (lastPos?.add(1, 0, 0) == posToCheck) return Motion.X
                    if (lastPos?.add(-1, 0, 0) == posToCheck) return Motion.mX
                }
            } else lastPos = EntityUtil.getRoundedBlockPos(mc.player)
            return null
        }
    }),
    Strafe2(object : ISpeedMode {
        override fun onEnable() {}
        override fun update() {
            if(MovementUtil.isMoving()) {
                if(mc.player.onGround) {
                    mc.player.jump()
                } else {
                    val direction = MovementUtil.getDirection()
                    mc.player.motionX = -sin(direction) * SpeedRewrite.motionXmodifier!!.valDouble
                    mc.player.motionZ = cos(direction) * SpeedRewrite.motionZmodifier!!.valDouble
                }
            }
        }
    }),
    Matrix(object : ISpeedMode {
        override fun onEnable() {}
        override fun update() {
            if(MovementUtil.isMoving()) {
                if(mc.player.onGround) {
                    mc.player.jump()
                } else {
                    MovementUtil.setMotion(MovementUtil.WALK_SPEED * 1.025)
                }
            }
        }
    });//TODO: NCP mode

    companion object {
        fun getCap(): Double {
            var ret = SpeedRewrite.cap!!.valDouble

            if(!SpeedRewrite.scaleCap!!.valBoolean) {
                return ret
            }

            if(mc.player.isPotionActive(MobEffects.SPEED)) {
                ret *= 1 + 0.2 * (mc.player.getActivePotionEffect(MobEffects.SPEED)?.amplifier!! + 1)
            }

            if(SpeedRewrite.slow!!.valBoolean && mc.player.isPotionActive(MobEffects.SLOWNESS)) {
                ret /= 1 + 0.2 * (mc.player.getActivePotionEffect(MobEffects.SLOWNESS)?.amplifier!! + 1)
            }

            return ret
        }
    }
}