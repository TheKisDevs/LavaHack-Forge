package com.kisman.cc.module.chat;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;

public class ChatAnimation extends Module {
    public static ChatAnimation instance;

    public ChatAnimation() {
        super("ChatAnimation", "ChatAnimation", Category.CHAT);

        instance = this;
    }
}
