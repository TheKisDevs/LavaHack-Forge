package com.kisman.cc.module.chat;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public class AntiSpamBypass extends Module {
    private Setting range = new Setting("Ragne", this, 100, 100, 10000, true);

    private Random random;

    public AntiSpamBypass() {
        super("AntiSpamBypass", "AntiSpamBypass", Category.CHAT);

        random = new Random();

        setmgr.rSetting(range);

        random.nextInt();
    }

    @SubscribeEvent
    public void onChat(ClientChatEvent event) {
        if(!event.getMessage().startsWith("/") ||
                !event.getMessage().startsWith(Kisman.instance.commandManager.cmdPrefixStr) ||
                !event.getMessage().startsWith(".") ||
                !event.getMessage().startsWith(",") ||
                !event.getMessage().startsWith(";") ||
                !event.getMessage().startsWith(":") ||
                !event.getMessage().startsWith("++") ||
                !event.getMessage().startsWith("--") ||
                !event.getMessage().startsWith("-") ||
                !event.getMessage().startsWith("+")
        ) {
            event.setMessage(event.getMessage() + " | " + random.nextInt());
        }
    }
}
