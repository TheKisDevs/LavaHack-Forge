package com.kisman.cc.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.common.MinecraftForge;

public class NoRotate extends Module {
    public NoRotate() {
        super("NoRotate", "NoRotate", Category.MOVEMENT);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(sendListener);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(sendListener);
    }

    @EventHandler
    private final Listener<PacketEvent.Send> sendListener = new Listener<>(event -> {
        if(event.getPacket() instanceof CPacketPlayer.Rotation) {
            event.cancel();
        }
    });
}
