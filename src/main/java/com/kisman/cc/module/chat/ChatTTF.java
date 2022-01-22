package com.kisman.cc.module.chat;

import com.kisman.cc.module.*;

public class ChatTTF extends Module {
    public static ChatTTF instance;

    public ChatTTF() {
        super("ChatTTF", Category.CHAT);

        instance = this;
    }
}
