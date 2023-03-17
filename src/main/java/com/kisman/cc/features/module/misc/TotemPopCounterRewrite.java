package com.kisman.cc.features.module.misc;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityStatus;

import java.util.HashMap;
import java.util.Map;

public class TotemPopCounterRewrite extends Module {
    public TotemPopCounterRewrite() {
        super("TotemPopCounterRew", "count totem pops but better!", Category.MISC);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(receive);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(receive);
    }

    private Map<String, Integer> playerTotemPops = new HashMap<>();
    @EventHandler
    private final Listener<PacketEvent.Receive> receive = new Listener<>(event -> {
        System.out.println(event.getPacket().getClass().getName());
        if(event.getPacket() instanceof SPacketEntityStatus && ((SPacketEntityStatus) event.getPacket()).getOpCode() == 35) {
            EntityPlayer entity = (EntityPlayer) ((SPacketEntityStatus) event.getPacket()).getEntity(mc.world);
            System.out.println(entity);
            String playerName = entity.getName();
            ItemStack mainHand = entity.getHeldItemMainhand();
            ItemStack offHand = entity.getHeldItemOffhand();
            int totemPops = 0;
            if (mainHand.getItem() == Items.TOTEM_OF_UNDYING || offHand.getItem() == Items.TOTEM_OF_UNDYING)
                totemPops++;
            int playerTotemPop = playerTotemPops.getOrDefault(playerName, 0);
            playerTotemPops.put(entity.getName(), playerTotemPop + totemPops);
            ChatUtility.warning().printClientModuleMessage(playerName + " popped " + playerTotemPops.get(playerName) + " totems!");
        }
    });
}
