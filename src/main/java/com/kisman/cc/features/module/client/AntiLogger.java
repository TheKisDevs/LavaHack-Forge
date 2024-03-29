package com.kisman.cc.features.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInfo;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.server.SPacketChat;

@ModuleInfo(
        name = "AntiLogger",
        desc = "Removes log4j messages",
        category = Category.CLIENT
)
public class AntiLogger extends Module {
    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.subscribe(listener);
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> listener = new Listener<>(event ->  {
        if(event.getPacket() instanceof SPacketChat) {
            String text = ((SPacketChat) event.getPacket()).getChatComponent().getUnformattedText();

            if(text.contains("jndi")) {
                event.cancel();

                ChatUtility.error().printClientModuleMessage("Removed Log4j exploit message");
            }
        }
    });
}
