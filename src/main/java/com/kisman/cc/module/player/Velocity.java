package com.kisman.cc.module.player;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventPlayerApplyCollision;
import com.kisman.cc.event.events.EventPlayerPushOutOfBlocks;
import com.kisman.cc.event.events.EventPlayerPushedByWater;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;

import com.kisman.cc.settings.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;

public class Velocity extends Module{
    private String mode;

    private boolean subscribing;

    private Setting packet = new Setting("Packet", this, "Packet");
    private Setting exp = new Setting("Explotion", this, true);
    private Setting bobbers = new Setting("Bobbers", this, true);
    private Setting noPush = new Setting("NoPush", this, true);

    public Velocity() {
        super("Velocity", "akb", Category.PLAYER);

        Kisman.instance.settingsManager.rSetting(new Setting("Mode", this, "Packet", new ArrayList<>(Arrays.asList("Packet", "Matrix1", "Matrix2"))));

        setmgr.rSetting(packet);
        setmgr.rSetting(exp);
        setmgr.rSetting(bobbers);
        setmgr.rSetting(noPush);
    }

    public void onEnable() {
        this.mode = Kisman.instance.settingsManager.getSettingByName(this, "Mode").getValString();

        if(this.mode.equalsIgnoreCase("Packet")) {
            this.subscribing = true;
            Kisman.EVENT_BUS.subscribe(receiveListener);
            Kisman.EVENT_BUS.subscribe(listener);
            Kisman.EVENT_BUS.subscribe(listener1);
            Kisman.EVENT_BUS.subscribe(listener2);
        }
    }

    public void update() {
        if(!this.subscribing) {
            if(this.mode.equalsIgnoreCase("Matrix1")) {
                if (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).getBlock() == Block.getBlockById(0)) {
                    if (mc.player.hurtTime > 0) {
                        float ticks = 0.2f;
                        mc.player.motionY = -ticks;
                        ticks += 1.5f;
                    }
                }
            } else if(mode.equalsIgnoreCase("Matrix2")) {
                if(mc.player.hurtTime > 8){
                    mc.player.onGround = true;
                }
            }
        }
    }

    public void onDisable() {
        if(subscribing) {
            this.subscribing = false;
            Kisman.EVENT_BUS.unsubscribe(receiveListener);
            Kisman.EVENT_BUS.unsubscribe(listener);
            Kisman.EVENT_BUS.unsubscribe(listener1);
            Kisman.EVENT_BUS.unsubscribe(listener2);
        }

    }

    @EventHandler
    private final Listener<EventPlayerApplyCollision> listener = new Listener<>(event -> {
        if(noPush.getValBoolean()) {
            event.cancel();
        }
    });

    @EventHandler
    private final Listener<EventPlayerPushedByWater> listener1 = new Listener<>(event -> {
        if(noPush.getValBoolean()) {
            event.cancel();
        }
    });

    @EventHandler
    private final Listener<EventPlayerPushOutOfBlocks> listener2 = new Listener<>(event -> {
        if(noPush.getValBoolean()) {
            event.cancel();
        }
    });

    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketEntityVelocity) {
            if(((SPacketEntityVelocity) event.getPacket()).getEntityID() == mc.player.getEntityId()) {
                event.cancel();
            }
        }

        if(event.getPacket() instanceof SPacketExplosion && exp.getValBoolean()) {
            event.cancel();
        }

        if(event.getPacket() instanceof SPacketEntityStatus && bobbers.getValBoolean()) {
            final SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            if(packet.getOpCode() == 31) {
                final Entity entity = packet.getEntity(mc.world);
                if(entity != null && entity instanceof EntityFishHook) {
                    final EntityFishHook fishHook = (EntityFishHook) entity;
                    if(fishHook.caughtEntity == mc.player) {
                        event.cancel();
                    }
                }
            }
        }
    });
}
