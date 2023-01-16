package com.kisman.cc.features.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventPlayerUpdate;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.mixin.mixins.accessor.AccessorEntityPlayer;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.number.NumberType;
import com.kisman.cc.util.TimerUtils;
import com.kisman.cc.util.entity.EntityUtil;
import com.kisman.cc.util.manager.Managers;
import com.kisman.cc.util.movement.MovementUtil;
import com.kisman.cc.util.world.WorldUtilKt;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("ConstantConditions")
public class Speed extends Module {
    public static Speed instance;

    public Setting speedMode = register(new Setting("SpeedMode", this, "Strafe", new ArrayList<>(Arrays.asList("Strafe", "Strafe New", "YPort", "Sti", "Matrix 6.4", "Matrix Bhop", "Sunrise Strafe", "Bhop", "Strafe2", "Matrix", "NCP", "Strafe3"/*, "Strafe4"*/))));

    private final Setting flagDetect = register(new Setting("Flag Detect", this, true));

    public final Setting useTimer = register(new Setting("Use Timer", this, false));
    public final Setting timerFactor = register(new Setting("Timer Factor", this, 1, 0, 10, true).setVisible(useTimer::getValBoolean));

    public final Setting motionXmodifier = register(new Setting("Motion X Modifier", this, 0, 0, 0.5, false).setVisible(() -> speedMode.checkValString("Strafe2")));
    public final Setting motionZmodifier = register(new Setting("Motion Z Modifier", this, 0, 0, 0.5, false).setVisible(() -> speedMode.checkValString("Strafe2")));

    public final Setting strafeSpeed = register(new Setting("Strafe Speed", this, 0.2873f, 0.1f, 1, false).setVisible(() -> speedMode.checkValString("Strafe New")));
    public final Setting slow = register(new Setting("Slow", this, false).setVisible(() -> speedMode.checkValString("Strafe New") || speedMode.checkValString("YPort")));
    public final Setting cap = register(new Setting("Cap", this, 10, 0, 10, false).setVisible(() -> speedMode.checkValString("Strafe New")));
    public final Setting scaleCap = register(new Setting("Scale Cap", this, false).setVisible(() -> speedMode.checkValString("Strafe New")));
    public final Setting lagTime = register(new Setting("Lag Time", this, 500, 0, 1000, NumberType.TIME).setVisible(() -> speedMode.checkValString("Strafe New")));

    private final Setting yPortSpeed = register(new Setting("YPortSpeed", this, 0.06f, 0.01f, 0.15f, false).setVisible(() -> speedMode.checkValString("YPort")));
    private final Setting yWater = register(new Setting("Water", this, false).setVisible(() -> speedMode.checkValString("YPort")));
    private final Setting yLava = register(new Setting("Lava", this, false).setVisible(() -> speedMode.checkValString("YPort")));

    private final Setting stiSpeed = register(new Setting("StiSpeed", this, 4, 0.1, 10, true).setVisible(() -> speedMode.checkValString("Sti")));

    public final Setting useMotion = register(new Setting("Use Motion", this, false).setVisible(() -> speedMode.checkValString("Bhop")));
    public final Setting useMotionInAir = register(new Setting("Use Motion In Air", this, false).setVisible(() -> speedMode.checkValString("Bhop")));
    public final Setting jumpMovementFactorSpeed = register(new Setting("Jump Movement Factor Speed", this, 0.265f, 0.01f, 10, false).setVisible(() -> speedMode.checkValString("Bhop")));
    public final Setting jumpMovementFactor = register(new Setting("Jump Movement Factor", this, false).setVisible(() -> speedMode.checkValString("Bhop")));
    public final Setting boostSpeed = register(new Setting("Boost Speed", this, 0.265f, 0.01f, 10, false).setVisible(() -> speedMode.checkValString("Bhop") || speedMode.checkValString("Strafe4")));
    public final Setting boostFactor = register(new Setting("Boost Factor", this, false).setVisible(() -> speedMode.checkValString("Bhop") || speedMode.checkValString("Strafe4")));

    private final Setting strict = register(new Setting("Strict", this, false).setVisible(() -> speedMode.checkValString("Strafe3") || speedMode.checkValString("Strafe4")));

    private int stage;
    private double speed;
    private double dist;
    private boolean boost;

    private int ncpStage;
    private double lastDist;

    private BlockPos lastPos;
    private double y = 1;

    private int strafe3Stage = 4;
    private double strafe3MotionSpeed;
    private double strafe3Distance;
    private boolean strafe3Flag;

    private int strafe4Stage = 0;
    private double strafe4CurrentSpeed = 0;
    private double strafe4PrevMotion = 0;
    private boolean strafe4Flag = false;

    private final TimerUtils velocityTimer = new TimerUtils();
    private double maxVelocity = 0;

    public Speed() {
        super("Speed", "speed", Category.MOVEMENT);
        super.setDisplayInfo(() -> "[" + speedMode.getValString() + "]");

        instance = this;
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(playerUpdate);
        Kisman.EVENT_BUS.subscribe(receive);
        reset();
    }

    private void reset() {
        EntityUtil.resetTimer();

        velocityTimer.reset();

        stage = 4;
        ncpStage = 0;
        strafe3Stage = 4;
        strafe3MotionSpeed = 0;
        strafe3Distance = 0;
        strafe3Flag = false;
        strafe4Stage = 4;
        strafe4PrevMotion = 0;
        strafe4CurrentSpeed = 0;

        maxVelocity = 0;

        if(mc.player == null || mc.world == null) return;
        dist = MovementUtil.getDistance2D();
        speed = MovementUtil.getSpeed();
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(playerUpdate);
        Kisman.EVENT_BUS.unsubscribe(receive);
        reset();
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        dist = MovementUtil.getDistance2D();

        if((speedMode.checkValString("Strafe New") || speedMode.checkValString("Strafe4")) && useTimer.getValBoolean() && Managers.instance.passed(250)) EntityUtil.setTimer(1.0808f + (0.008f * timerFactor.getValFloat()));

        if(mc.player.moveForward > 0 && mc.player.hurtTime < 5 && speedMode.getValString().equalsIgnoreCase("Strafe")) {
            if(mc.player.onGround) {
                mc.player.motionY = 0.405;
                float f = MovementUtil.getDirection();

                mc.player.motionX -= (MathHelper.sin(f) * 0.2F);
                mc.player.motionZ += (MathHelper.cos(f) * 0.2F);
            } else {
                double currentSpeed = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
                double speed = Math.abs(mc.player.rotationYawHead - mc.player.rotationYaw) < 90 ? 1.0064 : 1.001;
                double direction = MovementUtil.getDirection();

                mc.player.motionX = -Math.sin(direction) * speed * currentSpeed;
                mc.player.motionZ = Math.cos(direction) * speed * currentSpeed;
            }
        } else if(speedMode.getValString().equalsIgnoreCase("YPort")) doYPortSpeed();
        else if(speedMode.getValString().equalsIgnoreCase("Strafe New") && !mc.player.isElytraFlying()) {
            if(!Managers.instance.passed(lagTime.getValInt())) return;
            if(stage == 1 && MovementUtil.isMoving()) speed = 1.35 * MovementUtil.getSpeed(slow.getValBoolean(), strafeSpeed.getValDouble()) - 0.01;
            else if(stage == 2 && MovementUtil.isMoving() && mc.player.onGround) {
                mc.player.motionY = 0.3999 + MovementUtil.getJumpSpeed();
                speed *= boost ? 1.6835 : 1.395;
            } else if(stage == 3) {
                speed = dist  - 0.66 * (dist - MovementUtil.getSpeed(slow.getValBoolean(), strafeSpeed.getValDouble()));
                boost = !boost;
            } else {
                if((mc.world.getCollisionBoxes(null, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0)).size() > 0 || mc.player.collidedVertically) && stage > 0) stage = MovementUtil.isMoving() ? 1 : 0;
                speed = dist - dist / 159;
            }

            speed = Math.min(speed, getCap());
            speed = Math.max(speed, MovementUtil.getSpeed(slow.getValBoolean(), strafeSpeed.getValDouble()));
            MovementUtil.strafe((float) speed);

            if(MovementUtil.isMoving()) stage++;
        } else if(speedMode.getValString().equalsIgnoreCase("Matrix Bhop") && MovementUtil.isMoving()) {
            mc.gameSettings.keyBindJump.pressed = false;

            if (mc.player.onGround) {
                mc.player.jump();
                ((AccessorEntityPlayer) mc.player).setSpeedInAir(0.0208f);
                mc.player.jumpMovementFactor = 0.1f;
                EntityUtil.setTimer(0.94f);
            }
            if (mc.player.fallDistance > 0.6 && mc.player.fallDistance < 1.3) {
                ((AccessorEntityPlayer) mc.player).setSpeedInAir(0.0208f);
                EntityUtil.setTimer(1.8f);
            }
        } else if(speedMode.getValString().equalsIgnoreCase("Matrix 6.4")) {
            if (mc.player.ticksExisted % 4 == 0) mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
            if (!MovementUtil.isMoving()) return;
            if (mc.player.onGround) {
                mc.gameSettings.keyBindJump.pressed = false;
                mc.player.jump();
            } else if (mc.player.fallDistance <= 0.1) {
                ((AccessorEntityPlayer) mc.player).setSpeedInAir(0.0202f);
                mc.player.jumpMovementFactor = 0.027f;
                EntityUtil.setTimer(1.5f);
            } else if (mc.player.fallDistance > 0.1 && mc.player.fallDistance < 1.3) EntityUtil.setTimer(0.7f);
            else if (mc.player.fallDistance >= 1.3) {
                EntityUtil.resetTimer();
                ((AccessorEntityPlayer) mc.player).setSpeedInAir(0.0202f);
                mc.player.jumpMovementFactor = 0.025f;
            }
        } else if(speedMode.getValString().equalsIgnoreCase("Sunrise Strafe")) {
            if (MovementUtil.isMoving()) {
                if (mc.player.onGround) {
                    mc.player.jump();
                    MovementUtil.strafe(MovementUtil.getDirection(), MovementUtil.getSpeed() * 1.02);
                }
            } else {
                mc.player.motionX = 0.0;
                mc.player.motionZ = 0.0;
            }
        } else if(speedMode.getValString().equalsIgnoreCase("Bhop")) doBhop();
        else if(speedMode.getValString().equalsIgnoreCase("Strafe2") && MovementUtil.isMoving()) {
            if(mc.player.onGround) mc.player.jump();
            else {
                double yaw = MovementUtil.getDirection();
                mc.player.motionX = -Math.sin(yaw) * motionXmodifier.getValFloat();
                mc.player.motionZ = Math.cos(yaw) * motionZmodifier.getValFloat();
            }
        } else if(speedMode.getValString().equalsIgnoreCase("Matrix") && MovementUtil.isMoving() && mc.player.ticksExisted % 2 == 0) {
            if(mc.player.onGround) mc.player.jump();
            else MovementUtil.setMotion(MovementUtil.WALK_SPEED * 1.025);
        } else if(speedMode.checkValString("NCP") && !mc.player.isElytraFlying()) {
            switch(ncpStage) {
                case 0 : {
                    ncpStage++;
                    lastDist = 0;
                    break;
                }
                case 2 : {
                    if(MovementUtil.isMoving() && mc.player.onGround) {
                        mc.player.motionY = (isBoxColliding() ? 0.2 : 0.3999) + MovementUtil.getJumpSpeed();
                        speed *= 2.149;
                    }
                    break;
                }
                case 3 : {
                    speed = lastDist - (0.7095 * (lastDist - MovementUtil.getSpeed(slow.getValBoolean(), 0.2873)));
                    break;
                }
                default : {
                    if((collisionCheck() || mc.player.collidedHorizontally)) {
                        ncpStage = MovementUtil.isMoving() ? 0 : 1;
                    }

                    speed = lastDist - lastDist / 159;

                    break;
                }
            }

            speed = Math.min(speed, getCap());
            speed = Math.max(speed, MovementUtil.getSpeed(slow.getValBoolean(), 0.2873));
            MovementUtil.strafe((float) speed);
            ncpStage++;
        } else if(speedMode.checkValString("Strafe3")) {
            if(mc.player.onGround && MovementUtil.isMoving()) {
                strafe3Stage = 2;
            }

            if(strafe3Stage == 1 && MovementUtil.isMoving()) {
                strafe3Stage++;
//                strafe3MotionSpeed = 1.35 * getBaseMotionSpeed() - 0.01;
            } else if(strafe3Stage == 2) {
                strafe3Stage++;
                if(mc.player.onGround && MovementUtil.isMoving()) {
                    mc.player.motionY = MovementUtil.getJumpHeight(strict.getValBoolean());
                    strafe3MotionSpeed *= strafe3Flag ? 1.368 : 1.69;
                }
            } else if(stage == 3) {
                strafe3Stage++;
//                strafe3MotionSpeed = strafe3Distance * (0.66 * (strafe3Distance - getBaseMotionSpeed()));
            } else if(stage == 4) {
                if(collisionCheck() || mc.player.collidedVertically) stage = MovementUtil.isMoving() ? 1 : 0;
                strafe3MotionSpeed = strafe3Distance - strafe3Distance / 159;
                strafe3Flag = !strafe3Flag;
            }

//            strafe3MotionSpeed = Math.max(strafe3MotionSpeed, getBaseMotionSpeed());
            MovementUtil.strafe((float) strafe3MotionSpeed);
            if(!MovementUtil.isMoving()) {
                mc.player.motionX = 0;
                mc.player.motionY = 0;
            }
        } else if(speedMode.checkValString("Strafe4")) {
            if(MovementUtil.isMoving()) {
                double dX = mc.player.posX - mc.player.prevPosX;
                double dZ = mc.player.posZ - mc.player.prevPosZ;
                strafe4PrevMotion = Math.sqrt(dX * dX + dZ * dZ);
            }

            if (strafe4Stage != 1 || (mc.player.moveForward == 0.0f || mc.player.moveStrafing == 0.0f)) {
                if (strafe4Stage == 2 && (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f)) {
                    double jumpSpeed = 0.0D;

                    if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) jumpSpeed += (mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F;

                    mc.player.motionY = (0.3999D) + jumpSpeed;
                    strafe4CurrentSpeed *= strafe4Flag ? 1.6835D : 1.395D;
                } else if (strafe4Stage == 3) {
                    double adjustedMotion = 0.66D * (strafe4PrevMotion - MovementUtil.getBaseMoveSpeed());
                    strafe4CurrentSpeed = strafe4PrevMotion - adjustedMotion;
                    strafe4Flag = !strafe4Flag;
                } else {
                    List<AxisAlignedBB> collisionBoxes = mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0));
                    if ((collisionBoxes.size() > 0 || mc.player.collidedVertically) && strafe4Stage > 0) strafe4Stage = mc.player.moveForward == 0.0f && mc.player.moveStrafing == 0.0f ? 0 : 1;
                    strafe4CurrentSpeed = strafe4PrevMotion - strafe4PrevMotion / 159.0;
                }
            } else strafe4CurrentSpeed = 1.35D * MovementUtil.getBaseMoveSpeed() - 0.01D;

            strafe4CurrentSpeed = Math.max(strafe4CurrentSpeed, MovementUtil.getBaseMoveSpeed());

            if (maxVelocity > 0 && boostFactor.getValBoolean() && !velocityTimer.passedMillis(75) && !mc.player.collidedHorizontally) {
                strafe4CurrentSpeed = Math.max(strafe4CurrentSpeed, maxVelocity);
            } else if (strict.getValBoolean()) strafe4CurrentSpeed = Math.min(strafe4CurrentSpeed, 0.433D);

            if (!MovementUtil.isMoving()) {
                mc.player.motionX = 0;
                mc.player.motionY = 0;
            } else forward(strafe4CurrentSpeed);
            if (MovementUtil.isMoving()) return;

            strafe4Stage++;
        }
    }

    private void forward(double speed) {
        double[] motions = MovementUtil.forward(speed);
        mc.player.motionX = motions[0];
        mc.player.motionZ = motions[1];
    }

    public boolean isBoxColliding() {
        return mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, 0.21, 0.0)).size() > 0;
    }

    public boolean collisionCheck() {
        return mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0)).size() > 0;
    }

    private void doYPortSpeed() {
        if(!MovementUtil.isMoving() || (mc.player.isInWater() && !yWater.getValBoolean()) && (mc.player.isInLava() && !yLava.getValBoolean()) || mc.player.collidedHorizontally) return;
        if(mc.player.onGround) {
            EntityUtil.setTimer(1.15f);
            mc.player.jump();
            MovementUtil.setMotion(MovementUtil.DEFAULT_SPEED + yPortSpeed.getValInt());
        } else {
            mc.player.motionY = -1;
            EntityUtil.resetTimer();
        }
    }

    public double getCap() {
        double ret = cap.getValDouble();

        if (!scaleCap.getValBoolean()) return ret;
        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            int amplifier = Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            ret *= 1 + 0.2 * (amplifier + 1);
        }

        if (slow.getValBoolean() && mc.player.isPotionActive(MobEffects.SLOWNESS)) {
            int amplifier = Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SLOWNESS)).getAmplifier();
            ret /= 1 + 0.2 * (amplifier + 1);
        }

        return ret;
    }

    private void doBhop() {
        Motion currentMotion = getMotion();
        mc.player.setSprinting(true);

        if(mc.gameSettings.keyBindForward.isKeyDown()) {
            if(mc.player.onGround) {
                if(useMotion.getValBoolean() && currentMotion != null) {
                    switch (currentMotion) {
                        case mX:
                            mc.player.motionX=mc.player.motionX-0.1;
                            break;
                        case X:
                            mc.player.motionX=mc.player.motionX+0.1;
                            break;
                        case mY:
                            mc.player.motionY=mc.player.motionY-0.1;
                            break;
                        case Y:
                            mc.player.motionY=mc.player.motionY+0.1;
                            break;
                    }
                }
                y = 1;
                EntityUtil.resetTimer();
                if(useTimer.getValBoolean()) Managers.instance.timerManager.updateTimer(this, 2, 1.3f);
                mc.player.jump();
            } else {
                if(jumpMovementFactor.getValBoolean()) mc.player.jumpMovementFactor = jumpMovementFactorSpeed.getValFloat();
                if(y == 1) y = mc.player.getPositionVector().y;
                else {
                    if(mc.player.getPositionVector().y < y) {
                        y = mc.player.getPositionVector().y;
                        mc.player.motionX = 0;
                        mc.player.motionZ = 0;
                        if(useTimer.getValBoolean()) EntityUtil.resetTimer();
                        Managers.instance.timerManager.updateTimer(this, 2, 16);
                    } else {
                        y = mc.player.getPositionVector().y;
                        if(useMotionInAir.getValBoolean() && currentMotion != null) {
                            switch (currentMotion) {
                                case mX:
                                    mc.player.motionX = mc.player.motionX - 0.2;
                                    break;
                                case X:
                                    mc.player.motionX = mc.player.motionX + 0.2;
                                    break;
                                case mY:
                                    mc.player.motionY = mc.player.motionY - 0.2;
                                    break;
                                case Y:
                                    mc.player.motionY = mc.player.motionY + 0.2;
                                    break;
                            }
                        }
                    }
                }
            }
        }
    }

    private Motion getMotion() {
        BlockPos posToCheck = WorldUtilKt.playerPosition();
        if(getBlock(posToCheck) == Blocks.AIR && lastPos != null) {
            if(posToCheck != lastPos) {
                if(lastPos.add(0, 0, -1).equals(posToCheck)) return Motion.mY;
                if(lastPos.add(0, 0, 1).equals(posToCheck)) return Motion.Y;
                if(lastPos.add(1, 0, 0).equals(posToCheck)) return Motion.X;
                if(lastPos.add(-1, 0, 0).equals(posToCheck)) return Motion.mX;
            }
        } else lastPos = WorldUtilKt.playerPosition();
        return null;
    }

    private Block getBlock(BlockPos pos) {
        if(pos != null) return mc.world.getBlockState(pos).getBlock();
        return null;
    }

    public enum Motion {X,Y,mX,mY}

    @EventHandler
    private final Listener<PacketEvent.Receive> receive = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketPlayerPosLook) {
            if(flagDetect.getValBoolean()) {
                reset();
            }
        } else if(event.getPacket() instanceof SPacketExplosion) {
            SPacketExplosion explosion = (SPacketExplosion) event.getPacket();
            maxVelocity = Math.sqrt(explosion.getMotionX() * explosion.getMotionX() + explosion.getMotionZ() * explosion.getMotionZ());
            velocityTimer.reset();
        }
    }, 1000);

    @EventHandler private final Listener<EventPlayerUpdate> playerUpdate = new Listener<>(event -> {if(speedMode.getValString().equalsIgnoreCase("Sti")) mc.timer.tickLength = 50 / getSpeed();});
    private float getSpeed() {return Math.max((float) stiSpeed.getValDouble(), 0.1f);}
}
