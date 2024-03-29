package com.kisman.cc.features.module.misc;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;

public class AntiSpammer extends Module {
    public static AntiSpammer instance;

    public ArrayList<String> illegalWords = new ArrayList<>();

    public AntiSpammer() {
        super("AntiSpammer", Category.MISC);
        instance = this;
    }

    public boolean isBeta() {return true;}

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> listener = new Listener<>(event -> {
        if (event.getPacket() instanceof SPacketChat) {
            if (!((SPacketChat) event.getPacket()).isSystem()) return;
            String message = ((SPacketChat) event.getPacket()).chatComponent.getFormattedText();
            for(String str : illegalWords) message = message.replaceAll(str, "");
            ((SPacketChat) event.getPacket()).chatComponent = new TextComponentString(message);
        }
    });
}
