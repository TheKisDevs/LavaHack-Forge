package com.kisman.cc.module.movement;

import java.util.*;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.*;
import com.kisman.cc.mixin.mixins.accessor.AccessorEntityPlayer;
import com.kisman.cc.module.*;
import com.kisman.cc.oldclickgui.csgo.components.Slider;
import com.kisman.cc.settings.*;

import com.kisman.cc.util.*;
import com.kisman.cc.util.manager.Managers;
import i.gishreloaded.gishcode.utils.Utils;
import me.zero.alpine.listener.*;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

public class Speed extends Module {
    public static Speed instance;

    private float yPortSpeed;

    public Setting speedMode = new Setting("SpeedMode", this, "Strafe", new ArrayList<>(Arrays.asList("Strafe", "Strafe New", "YPort", "Sti", "Matrix 6.4", "Matrix Bhop", "Sunrise Strafe")));

    private Setting strafeNewLine = new Setting("StrafeNewLine", this, "Strafe New");
    private Setting strafeSpeed = new Setting("Strafe Speed", this, 0.2873f, 0.1f, 1, false);
    private Setting useTimer = new Setting("Use Timer", this, false);
    private Setting slow = new Setting("Slow", this, false);
    private Setting cap = new Setting("Cap", this, 10, 0, 10, false);
    private Setting scaleCap = new Setting("Scale Cap", this, false);
    private Setting lagTime = new Setting("Lag Time", this, 500, 0, 1000, Slider.NumberType.TIME);

    private Setting yPortLine = new Setting("YPortLine", this, "YPort");
    private Setting yWater = new Setting("Water", this, false);
    private Setting yLava = new Setting("Lava", this, false);

    private Setting stiLine = new Setting("StiLine", this, "Sti");
    private Setting stiSpeed = new Setting("StiSpeed", this, 4, 0.1, 10, true);

    private int stage;
    private double speed;
    private double dist;
    private boolean boost;

    public Speed() {
        super("Speed", "speed", Category.MOVEMENT);

        instance = this;

        setmgr.rSetting(speedMode);

        setmgr.rSetting(strafeNewLine);
        setmgr.rSetting(strafeSpeed);
        setmgr.rSetting(useTimer);
        setmgr.rSetting(slow);
        setmgr.rSetting(cap);
        setmgr.rSetting(scaleCap);
        setmgr.rSetting(lagTime);

        setmgr.rSetting(yPortLine);
        Kisman.instance.settingsManager.rSetting(new Setting("YPortSpeed", this, 0.06f, 0.01f, 0.15f, false));
        setmgr.rSetting(yWater);
        setmgr.rSetting(yLava);

        setmgr.rSetting(stiLine);
        setmgr.rSetting(stiSpeed);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
        Kisman.EVENT_BUS.subscribe(listener1);
        stage = 4;
        dist = MovementUtil.getDistance2D();
        speed = MovementUtil.getSpeed();
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
        Kisman.EVENT_BUS.unsubscribe(listener1);

        EntityUtil.resetTimer();

        mc.timer.tickLength = 50;
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        super.setDisplayInfo("[" + speedMode.getValString() + TextFormatting.GRAY + "]");

        this.yPortSpeed = (float) Kisman.instance.settingsManager.getSettingByName(this, "YPortSpeed").getValDouble();

        dist = MovementUtil.getDistance2D();

        if(mc.player.moveForward > 0 && mc.player.hurtTime < 5 && speedMode.getValString().equalsIgnoreCase("Strafe")) {
            if(mc.player.onGround) {
                mc.player.motionY = 0.405;
                float f = Utils.getDirection();

                mc.player.motionX -= (MathHelper.sin(f) * 0.2F);
                mc.player.motionZ += (MathHelper.cos(f) * 0.2F);
            } else {
                double currentSpeed = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
                double speed = Math.abs(mc.player.rotationYawHead - mc.player.rotationYaw) < 90 ? 1.0064 : 1.001;
                double direction = Utils.getDirection();

                mc.player.motionX = -Math.sin(direction) * speed * currentSpeed;
                mc.player.motionZ = Math.cos(direction) * speed * currentSpeed;
            }
        } else if(speedMode.getValString().equalsIgnoreCase("YPort")) doYPortSpeed();
        else if(speedMode.getValString().equalsIgnoreCase("Strafe New") && !mc.player.isElytraFlying()) {
            if(useTimer.getValBoolean() && Managers.instance.passed(250)) EntityUtil.setTimer(1.0888f);
            if(!Managers.instance.passed(lagTime.getValInt())) return;
            if(stage == 1 && PlayerUtil.isMoving(mc.player)) speed = 1.35 * MovementUtil.getSpeed(slow.getValBoolean(), strafeSpeed.getValDouble()) - 0.01;
            else if(stage == 2 && PlayerUtil.isMoving(mc.player)) {
                mc.player.motionY = 0.3999 + MovementUtil.getJumpSpeed();
                speed *= boost ? 1.6835 : 1.395;
            } else if(stage == 3) {
                speed = dist  - 0.66 * (dist - MovementUtil.getSpeed(slow.getValBoolean(), strafeSpeed.getValDouble()));
                boost = !boost;
            } else {
                if((mc.world.getCollisionBoxes(null, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0)).size() > 0 || mc.player.collidedVertically) && stage > 0) stage = PlayerUtil.isMoving(mc.player) ? 1 : 0;
                speed = dist - dist / 159;
            }

            speed = Math.min(speed, getCap());
            speed = Math.max(speed, MovementUtil.getSpeed(slow.getValBoolean(), strafeSpeed.getValDouble()));
            MovementUtil.strafe((float) speed);

            if(PlayerUtil.isMoving(mc.player)) stage++;
        } else if(speedMode.getValString().equalsIgnoreCase("Matrix Bhop") && PlayerUtil.isMoving(mc.player)) {
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
            if (mc.player.ticksExisted % 4 == 0) mc.getConnection().sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
            if (!PlayerUtil.isMoving(mc.player)) return;
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
            if (PlayerUtil.isMoving(mc.player)) {
                if (mc.player.onGround) {
                    mc.player.jump();
                    MovementUtil.strafe(MovementUtil.calcMoveYaw(mc.player.rotationYaw), MovementUtil.getSpeed() * 1.02);
                }
            } else {
                mc.player.motionX = 0.0;
                mc.player.motionZ = 0.0;
            }
        }
    }

    private void doYPortSpeed() {
        if(!PlayerUtil.isMoving(mc.player) || (mc.player.isInWater() && !yWater.getValBoolean()) && (mc.player.isInLava() && !yLava.getValBoolean()) || mc.player.collidedHorizontally) return;
        if(mc.player.onGround) {
            EntityUtil.setTimer(1.15f);
            mc.player.jump();
            PlayerUtil.setSpeed(mc.player, PlayerUtil.getBaseMoveSpeed() + this.yPortSpeed);
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

    @EventHandler
    private final Listener<PacketEvent.Receive> listener1 = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketPlayerPosLook) {
            if(mc.player != null) dist = 0;
            speed = 0;
            stage = 4;
            EntityUtil.setTimer(1);
        }
    });

    @EventHandler private final Listener<EventPlayerUpdate> listener = new Listener<>(event -> {if(speedMode.getValString().equalsIgnoreCase("Sti")) mc.timer.tickLength = 50 / getSpeed();});
    private float getSpeed() {return Math.max((float) stiSpeed.getValDouble(), 0.1f);}
}
