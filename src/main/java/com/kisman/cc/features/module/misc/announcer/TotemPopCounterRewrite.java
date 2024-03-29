package com.kisman.cc.features.module.misc.announcer;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInfo;
import com.kisman.cc.util.UtilityKt;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;

import java.util.HashMap;
import java.util.Map;

@ModuleInfo(
        name = "TotemPopCounter",
        display = "Pops",
        desc = "count totem pops but better!",
        submodule = true
)
public class TotemPopCounterRewrite extends Module {
    private final Map<String, Integer> pops = new HashMap<>();

    public void onEnable() {
        super.onEnable();
        Kisman.EVENT_BUS.subscribe(receive);
    }

    public void onDisable() {
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(receive);
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> receive = new Listener<>(event -> {
        if(event.getPacket() instanceof SPacketEntityStatus && ((SPacketEntityStatus) event.getPacket()).getOpCode() == 35) {
            Entity entity = ((SPacketEntityStatus) event.getPacket()).getEntity(mc.world);

            if(entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                pops.put(player.getName(), pops.getOrDefault(player.getName(), 0) + 1);
                ChatUtility.message().printClientMessage(player.getName() + " popped " + pops.get(player.getName()) + " totems!", UtilityKt.string2int(player.getName()) + moduleId);
            }
        }
    });
}
