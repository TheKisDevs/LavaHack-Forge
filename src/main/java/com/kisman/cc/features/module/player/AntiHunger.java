package com.kisman.cc.features.module.player;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.PingBypassModule;
import com.kisman.cc.settings.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;


@PingBypassModule
public class AntiHunger extends Module {
    public static AntiHunger INSTANCE;

    public AntiHunger() {
        super("AntiHunger", "Attempts to negate hunger loss", Category.PLAYER);
        INSTANCE = this;
    }


    public Setting stopSprint = new Setting("StopSprint", this, true);
    public Setting groundSpoof = new Setting("GroundSpoof",this, true);

    private boolean previousSprint;


    @Override
    public void onEnable() {

        if (mc.player.isSprinting() || mc.player.isSprinting()) {
            previousSprint = true;
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));

        }

        super.onEnable();
        Kisman.EVENT_BUS.subscribe(send);

    }

    @Override
    public void onDisable() {
        if(mc.player == null || mc.world == null) return;
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(send);


        if (previousSprint) {
            previousSprint = false;
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
        }
    }

    @EventHandler
    private final Listener<PacketEvent.Send> send = new Listener<>(event -> {

        if (event.getPacket() instanceof CPacketPlayer) {

            if (groundSpoof.getValBoolean()) {

                if (!mc.player.isRiding() && !mc.player.isElytraFlying()) {

                    ((CPacketPlayer) event.getPacket()).onGround = true;
                }
            }
        }

        else if (event.getPacket() instanceof CPacketEntityAction) {

            CPacketEntityAction packet = (CPacketEntityAction) event.getPacket();

            if (packet.getAction().equals(CPacketEntityAction.Action.START_SPRINTING) || packet.getAction().equals(CPacketEntityAction.Action.STOP_SPRINTING)) {

                if (stopSprint.getValBoolean()) {

                    event.cancel();
                }
            }
        }
    })
;}