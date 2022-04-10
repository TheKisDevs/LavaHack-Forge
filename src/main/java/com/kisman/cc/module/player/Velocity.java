package com.kisman.cc.module.player;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.*;
import com.kisman.cc.module.*;

import com.kisman.cc.settings.Setting;
import me.zero.alpine.listener.*;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.network.play.server.*;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;

public class Velocity extends Module{
    private String mode;

    private boolean subscribing;

    private Setting packet = new Setting("Packet", this, "Packet");
    private Setting exp = new Setting("Explosion", this, true);
    private Setting bobbers = new Setting("Bobbers", this, true);
    private Setting noPush = new Setting("NoPush", this, true);

    public Velocity() {
        super("Velocity", "akb", Category.PLAYER);

        Kisman.instance.settingsManager.rSetting(new Setting("Mode", this, "Packet", new ArrayList<>(Arrays.asList("Packet", "Matrix", "Matrix 6.4"))));

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
        this.mode = Kisman.instance.settingsManager.getSettingByName(this, "Mode").getValString();


        super.setDisplayInfo("[" + mode + "]");
        if(!this.subscribing && mc.player != null && mc.world != null) {
            if(this.mode.equalsIgnoreCase("Matrix")) if (mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).getBlock() == Block.getBlockById(0)) if (mc.player.hurtTime > 0) mc.player.motionY = -0.2;
             else if(mode.equalsIgnoreCase("Matrix 6.4")) if(mc.player.hurtTime > 8) mc.player.onGround = true;
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
        if(noPush.getValBoolean()) event.cancel();
    });

    @EventHandler
    private final Listener<EventPlayerPushedByWater> listener1 = new Listener<>(event -> {
        if(noPush.getValBoolean()) event.cancel();
    });

    @EventHandler
    private final Listener<EventPlayerPushOutOfBlocks> listener2 = new Listener<>(event -> {
        if(noPush.getValBoolean()) event.cancel();
    });

    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketEntityVelocity) if(((SPacketEntityVelocity) event.getPacket()).getEntityID() == mc.player.getEntityId()) event.cancel();
        if(event.getPacket() instanceof SPacketExplosion && exp.getValBoolean()) event.cancel();
        if(event.getPacket() instanceof SPacketEntityStatus && bobbers.getValBoolean()) {
            final SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            if(packet.getOpCode() == 31) {
                final Entity entity = packet.getEntity(mc.world);
                if(entity instanceof EntityFishHook) if(((EntityFishHook) entity).caughtEntity == mc.player) event.cancel();
            }
        }
    });
}
