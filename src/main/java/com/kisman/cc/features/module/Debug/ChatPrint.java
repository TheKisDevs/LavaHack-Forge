package com.kisman.cc.features.module.Debug;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.util.TimerUtils;
import com.kisman.cc.util.chat.cubic.ChatUtility;

/**
 * Test class for displaying messages in chat
 */
public class ChatPrint extends Module {

    public ChatPrint(){
        super("ChatPrint", Category.DEBUG);
        // init chat utility
    }

    private final TimerUtils timer = new TimerUtils();

    @Override
    public void onEnable(){
        ChatUtility.info();
    }

    public void update(){
        if(mc.player == null || mc.world == null)
            return;

        if(timer.passedMillis(5000)){
            ChatUtility.info().printClientModuleMessage("5000 ms passed");
            ChatUtility.complete().printClassMessage("5000 ms complete");
            ChatUtility.message().printModuleMessage("5000 ms");
            ChatUtility.error().printClientClassMessage("5000 ms");
            ChatUtility.warning().printMessage("warn 5000 ms");
            timer.reset();
        }
    }
}
