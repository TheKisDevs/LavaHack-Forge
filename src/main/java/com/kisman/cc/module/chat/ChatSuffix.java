package com.kisman.cc.module.chat;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatSuffix extends Module {
    public ChatSuffix() {
        super("ChatSuffix", "green", Category.CHAT);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onChat(ClientChatEvent event) {
        event.setMessage(event.getMessage() + " | " + Kisman.getName() + " own you and all");
    }
}
