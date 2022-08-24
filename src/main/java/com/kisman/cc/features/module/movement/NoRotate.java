package com.kisman.cc.features.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.number.NumberType;
import com.kisman.cc.util.TimerUtils;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import me.zero.alpine.listener.*;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

@PingBypassModule
public class NoRotate extends Module {
    private final Setting waitDelay = register(new Setting("Delay", this, 2500, 0, 10000, NumberType.TIME));

    private final TimerUtils timer = new TimerUtils();
    private boolean cancelPackets = true;
    private boolean timerReset = false;

    public NoRotate() {
        super("NoRotate", "NoRotate", Category.MOVEMENT);
    }

    public void onEnable() {
        super.onEnable();
        Kisman.EVENT_BUS.subscribe(sendListener);

        ChatUtility.warning().printClientModuleMessage("This module might desync you!");
    }

    public void onDisable() {
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(sendListener);
    }

    public void update() {
        if(timerReset && !cancelPackets && timer.passedMillis(waitDelay.getValInt())) {
            cancelPackets = true;
            timerReset = false;
        }
    }

    @SubscribeEvent
    public void onConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        timer.reset();
        timerReset = true;
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        cancelPackets = false;
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> sendListener = new Listener<>(event -> {
        if(cancelPackets && event.getPacket() instanceof SPacketPlayerPosLook) {
            SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
            packet.yaw = mc.player.rotationYaw;
            packet.pitch = mc.player.rotationPitch;
        }
    });
}
