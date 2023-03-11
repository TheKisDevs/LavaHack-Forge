package com.kisman.cc.features.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.*;
import io.netty.buffer.Unpooled;
import me.zero.alpine.listener.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;

@ModuleInfo(
        name = "ForgeBypass",
        desc = "think yourself",
        category = Category.CLIENT
)
public class ForgeBypass extends Module {
    @ModuleInstance
    public static ForgeBypass instance;

    public ForgeBypass() {
        super();
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(send);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(send);
    }

    @EventHandler
    private final Listener<PacketEvent.Send> send = new Listener<>(event -> {
        if (!mc.isIntegratedServerRunning()) {
            if (event.getPacket().getClass().getName().equals("net.minecraftforge.fml.common.network.internal.FMLProxyPacket")) event.cancel();
            else if (event.getPacket() instanceof CPacketCustomPayload) if (((CPacketCustomPayload) event.getPacket()).getChannelName().equalsIgnoreCase("MC|Brand")) ((CPacketCustomPayload) event.getPacket()).data = (new PacketBuffer(Unpooled.buffer()).writeString("vanilla"));
        }
    });
}
