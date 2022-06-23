package com.kisman.cc.features.module.player;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.BlockPushEvent;
import com.kisman.cc.event.events.EventPlayerMove;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.entity.PacketUtil;
import com.kisman.cc.util.entity.player.PlayerUtil;
import com.kisman.cc.util.movement.MovementUtil;
import me.zero.alpine.event.type.Cancellable;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FreeCamRewrite extends Module {
    public static FreeCamRewrite instance;

    private final Setting speed = register(new Setting("Speed", this, 1, 0, 10, false));
    private EntityOtherPlayerMP fakePlayer;

    public FreeCamRewrite() {
        super("FreeCamRewrite", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Kisman.EVENT_BUS.subscribe(listener);
        Kisman.EVENT_BUS.subscribe(listener1);
        Kisman.EVENT_BUS.subscribe(listener2);
        Kisman.EVENT_BUS.subscribe(listener3);

        if ( mc.player == null ) {
            super.setToggled(false);
        }
        mc.player.dismountRidingEntity();
        fakePlayer = PlayerUtil.createFakePlayerAndAddToWorld(mc.player.getGameProfile());
        fakePlayer.onGround = mc.player.onGround;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(listener);
        Kisman.EVENT_BUS.unsubscribe(listener1);
        Kisman.EVENT_BUS.unsubscribe(listener2);
        Kisman.EVENT_BUS.unsubscribe(listener3);

        if ( mc.player == null ) return;
        mc.player.setPosition(fakePlayer.posX, fakePlayer.posY, fakePlayer.posZ);
        mc.player.noClip = false;
        PlayerUtil.removeFakePlayer(fakePlayer);
        fakePlayer = null;
    }

    @SubscribeEvent
    public void onEntityJoin(EntityJoinWorldEvent event) {
        if(event.getEntity() == mc.player) super.setToggled(false);
    }

    public EntityOtherPlayerMP getPlayer() {
        return fakePlayer;
    }

    public void rotate(float yaw, float pitch) {
        if ( fakePlayer != null ) {
            fakePlayer.rotationYawHead = yaw;
            fakePlayer.setPositionAndRotation(fakePlayer.posX,
                    fakePlayer.posY,
                    fakePlayer.posZ,
                    yaw,
                    pitch);

            fakePlayer.setPositionAndRotationDirect(fakePlayer.posX,
                    fakePlayer.posY,
                    fakePlayer.posZ,
                    yaw,
                    pitch,
                    3,
                    false);
        }
    }

    @EventHandler
    private final Listener<EventPlayerMove> listener = new Listener<>(event -> {
        mc.player.noClip = true;
    });

    @EventHandler
    private final Listener<PacketEvent.Send> listener1 = new Listener<>(event -> {
        if ( event.getPacket() instanceof CPacketPlayer ) event.cancel();
    });

    @EventHandler
    private final Listener<PacketEvent.Receive> listener2 = new Listener<>(event -> {
        mc.addScheduledTask(() ->
        {
            if (mc.player == null) return;

            PacketUtil.handlePosLook((SPacketPlayerPosLook) event.getPacket(),
                    getPlayer() == null
                            ? mc.player
                            : getPlayer(),
                    false);

            getPlayer().onGround = true;
        });

        event.cancel();
    });

    @EventHandler
    private final Listener<BlockPushEvent> listener3 = new Listener<>(Cancellable::cancel);

    public void update() {
        mc.player.noClip = true;
        mc.player.setVelocity(0, 0, 0);
        mc.player.jumpMovementFactor = (float) speed.getValDouble();
        double[] dir = MovementUtil.strafe(speed.getValDouble());
        if (mc.player.movementInput.moveStrafe != 0 || mc.player.movementInput.moveForward != 0) {
            mc.player.motionX = dir[0];
            mc.player.motionZ = dir[1];
        } else {
            mc.player.motionX = 0;
            mc.player.motionZ = 0;
        }

        mc.player.setSprinting(false);

        if (mc.gameSettings.keyBindJump.isKeyDown()) mc.player.motionY += speed.getValDouble();
        if (mc.gameSettings.keyBindSneak.isKeyDown()) mc.player.motionY -= speed.getValDouble();
    }
}


