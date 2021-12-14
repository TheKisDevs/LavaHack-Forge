package com.kisman.cc.module.chat;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoGlobal extends Module {
    public AutoGlobal() {
        super("AutoGlobal", "for nn servers", Category.CHAT);
    }

    @SubscribeEvent
    public void onChat(ClientChatEvent event) {
        if(!event.getMessage().startsWith("/") &&
                !event.getMessage().startsWith(Kisman.instance.commandManager.cmdPrefixStr) &&
                !event.getMessage().startsWith(".") &&
                !event.getMessage().startsWith(",") &&
                !event.getMessage().startsWith(";") &&
                !event.getMessage().startsWith(":") &&
                !event.getMessage().startsWith("++") &&
                !event.getMessage().startsWith("--") &&
                !event.getMessage().startsWith("-") &&
                !event.getMessage().startsWith("+")
        ) {
            event.setMessage("!" + event.getMessage());
        }
    }
}
