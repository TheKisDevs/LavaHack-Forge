package com.kisman.cc.features.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.Event;
import com.kisman.cc.event.events.EventPlayerMotionUpdate;
import com.kisman.cc.event.events.EventPlayerMove;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInfo;
import com.kisman.cc.features.module.ModuleInstance;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.TimerUtils;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

@ModuleInfo(
        name = "Strafe",
        category = Category.MOVEMENT
)
public class Strafe extends Module {
    @ModuleInstance
    public static Strafe instance;

    private final Setting speed = register(new Setting("Speed", this, 0.2873, 0.05, 1, false));
    private final Setting potionMultiplier = register(new Setting("PotionMultiplier", this, 1, 0.1, 5, false));
    private final Setting strict = register(new Setting("Strict", this, false));
    private final Setting sprint = register(new Setting("Sprint", this, false));
    private final Setting boost = register(new Setting("Boost", this, false));
    private final Setting inLiquids = register(new Setting("InLiquids", this, false));

    private double curSpeed = 0;
    private double prevMotion = 0;
    private double maxVelocity = 0;
    private boolean oddStage = false;
    private int state = 4;

    private final TimerUtils velocityTimer = timer();

    private boolean sneaking = false;

    @Override
    public void onEnable() {
        if(mc.player == null || mc.world == null){
            toggle();
            return;
        }

        maxVelocity = 0;
        state = 4;
        curSpeed = getBaseSpeed();
        prevMotion = 0;

        Kisman.EVENT_BUS.subscribe(this);
    }

    @Override
    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(this);
    }

    @Override
    public void update() {
        if(mc.player == null || mc.world == null) return;

        if(sprint.getValBoolean() && !mc.player.isSprinting() && (mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown())){
            mc.player.setSprinting(true);
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
        }

        if(strict.getValBoolean() && shouldSneak()){
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            sneaking = true;
        }

        if(strict.getValBoolean() && shouldNotSneak()){
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            sneaking = false;
        }
    }

    @EventHandler
    private final Listener<EventPlayerMotionUpdate> motionUpdateListener = new Listener<>(event -> {
        if(event.getEra() == Event.Era.POST) return;

        if(!(mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown())){
            curSpeed = 0;
        }

        double dX = mc.player.posX - mc.player.prevPosX;
        double dZ = mc.player.posZ - mc.player.prevPosZ;
        prevMotion = Math.sqrt(dX * dX + dZ * dZ);
    });

    @EventHandler
    private final Listener<PacketEvent.Receive> packetListener = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketPlayerPosLook){
            curSpeed = 0;
            prevMotion = 0;
            maxVelocity = 0;
            return;
        }

        if(event.getPacket() instanceof SPacketExplosion){
            SPacketExplosion packet = (SPacketExplosion) event.getPacket();
            maxVelocity = Math.sqrt(packet.getMotionX() * packet.getMotionX() + packet.getMotionZ() * packet.getMotionZ());
            velocityTimer.reset();
        }
    });

    @EventHandler
    private final Listener<EventPlayerMove> moveListener = new Listener<>(event -> {
        if(mc.player.isInWater() || mc.player.isInLava() && !inLiquids.getValBoolean()) return;

        event.cancel();

        if (state != 1 || (mc.player.moveForward == 0.0f || mc.player.moveStrafing == 0.0f)) {
            if (state == 2 && (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f)) {
                double jumpSpeed = 0.0D;

                if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
                    jumpSpeed += (mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F;
                }

                mc.player.motionY = 0.3999D + jumpSpeed;
                event.y = mc.player.motionY;
                curSpeed *= oddStage ? 1.6835D : 1.395D;
            } else if (state == 3) {
                double adjustedMotion = 0.66D * (prevMotion - getBaseSpeed());
                curSpeed= prevMotion - adjustedMotion;
                oddStage = !oddStage;
            } else {
                List<AxisAlignedBB> collisionBoxes = mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0));
                if ((collisionBoxes.size() > 0 || mc.player.collidedVertically) && state > 0) {
                    state = mc.player.moveForward == 0.0f && mc.player.moveStrafing == 0.0f ? 0 : 1;
                }
                curSpeed = prevMotion - prevMotion / 159.0;
            }
        } else {
            curSpeed = 1.35D * getBaseSpeed() - 0.01D;
        }

        curSpeed = Math.max(curSpeed, getBaseSpeed());

        if (maxVelocity > 0 && boost.getValBoolean() && !velocityTimer.passedMillis(75) && !mc.player.collidedHorizontally) {
            curSpeed = Math.max(curSpeed, maxVelocity);
        } else if (strict.getValBoolean()) {
            curSpeed = Math.min(curSpeed, 0.433D);
        }

        double forward = mc.player.movementInput.moveForward;
        double strafe = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;

        if (forward == 0.0D && strafe == 0.0D) {
            event.x = 0;
            event.z = 0;
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                    yaw += (float)(forward > 0.0D ? -45 : 45);
                } else if (strafe < 0.0D) {
                    yaw += (float)(forward > 0.0D ? 45 : -45);
                }

                strafe = 0.0D;

                if (forward > 0.0D) {
                    forward = 1.0D;
                } else if (forward < 0.0D) {
                    forward = -1.0D;
                }
            }

            event.x = forward * curSpeed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * curSpeed * Math.sin(Math.toRadians(yaw + 90.0F));
            event.z = forward * curSpeed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * curSpeed * Math.cos(Math.toRadians(yaw + 90.0F));
        }


        if (mc.player.moveForward == 0.0f && mc.player.moveStrafing == 0.0f) {
            return;
        }

        state++;
    });

    private boolean shouldSneak(){
        Item item = mc.player.getActiveItemStack().getItem();
        boolean handActive = mc.player.isHandActive() && (item instanceof ItemFood || item instanceof ItemBook || item instanceof ItemPotion);
        return !sneaking && handActive;
    }

    private boolean shouldNotSneak(){
        Item item = mc.player.getActiveItemStack().getItem();
        boolean handActive = mc.player.isHandActive() && (item instanceof ItemFood || item instanceof ItemBook || item instanceof ItemPotion);
        return sneaking && !handActive;
    }

    private double getBaseSpeed(){
        double baseSpeed = speed.getValDouble();
        if(mc.player.isPotionActive(MobEffects.SPEED)){
            double amplifier = mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier();
            baseSpeed *= (1 + 0.2 * (amplifier + 1)) * potionMultiplier.getValDouble();
        }
        return baseSpeed;
    }
}
