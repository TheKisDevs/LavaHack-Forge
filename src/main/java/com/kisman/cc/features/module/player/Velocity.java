package com.kisman.cc.features.module.player;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventPlayerApplyCollision;
import com.kisman.cc.event.events.EventPlayerPushOutOfBlocks;
import com.kisman.cc.event.events.EventPlayerPushedByWater;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInfo;
import com.kisman.cc.settings.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;

@ModuleInfo(
        name = "Velocity",
        category = Category.PLAYER
)
public class Velocity extends Module {
    private final Setting exp = register(new Setting("Explosion", this, true));
    private final Setting bobbers = register(new Setting("Bobbers", this, true));
    private final Setting noPush = register(new Setting("NoPush", this, true));

    private final Setting knockback = register(new Setting("KnockBack", this, false));
    private final Setting horizontal = register(new Setting("Horizontal", this, 0, 0, 100, true));
    private final Setting vertical = register(new Setting("Vertical", this, 0, 0, 100, true));

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(receiveListener);
        Kisman.EVENT_BUS.subscribe(listener);
        Kisman.EVENT_BUS.subscribe(listener1);
        Kisman.EVENT_BUS.subscribe(listener2);
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;

        if(knockback.getValBoolean() && mc.player.hurtTime == mc.player.maxHurtTime) {
            mc.player.motionX *= (double) horizontal.getValInt() / 100;
            mc.player.motionY *= (double) vertical.getValInt() / 100;
            mc.player.motionZ *= (double) horizontal.getValInt() / 100;
        }
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(receiveListener);
        Kisman.EVENT_BUS.unsubscribe(listener);
        Kisman.EVENT_BUS.unsubscribe(listener1);
        Kisman.EVENT_BUS.unsubscribe(listener2);
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
        if(event.getPacket() instanceof SPacketEntityVelocity && ((SPacketEntityVelocity) event.getPacket()).getEntityID() == mc.player.getEntityId()) event.cancel();
        if(event.getPacket() instanceof SPacketExplosion && exp.getValBoolean()) event.cancel();
        if(event.getPacket() instanceof SPacketEntityStatus && bobbers.getValBoolean()) {
            SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            if(packet.getOpCode() == 31) {
                Entity entity = packet.getEntity(mc.world);
                if(entity instanceof EntityFishHook) if(((EntityFishHook) entity).caughtEntity == mc.player) event.cancel();
            }
        }
    });
}
