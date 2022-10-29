package com.kisman.cc.features.module.misc;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.status.client.CPacketPing;
import net.minecraft.network.status.server.SPacketPong;

public class PacketDelay extends Module {

    public PacketDelay(){
        super("PacketDelay", "Measures the delay a packet needs to be send and received back", Category.MISC, 0, true);
    }

    private long start = 0;

    @Override
    public void onEnable(){
        if(mc.player == null || mc.world == null)
            return;

        ChatUtility.info().printClientModuleMessage("Sending packet...");
        mc.player.connection.sendPacket(new CPacketPing());
        this.start = System.currentTimeMillis();
        Kisman.EVENT_BUS.subscribe(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(this);
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> listener = new Listener<>(event -> {
        if(!(event.getPacket() instanceof SPacketPong))
            return;
        long end = System.currentTimeMillis();
        ChatUtility.info().printClientModuleMessage("The delay was: " + (end - start) + "ms");
        start = 0;
        this.toggle();
    });
}
