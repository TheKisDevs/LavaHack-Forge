package com.kisman.cc.features.module.player;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Beta;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

import java.lang.reflect.Field;

@Beta
public class PacketLogger extends Module{
    private final Setting client = register(new Setting("Client", this, true));
    private final Setting server = register(new Setting("Server", this, true));
    private final Setting values = register(new Setting("Values", this, false));

    public PacketLogger() {
        super("PacketLogger", Category.PLAYER);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
        Kisman.EVENT_BUS.subscribe(listener1);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
        Kisman.EVENT_BUS.unsubscribe(listener1);
    }

    @EventHandler
    private final Listener<PacketEvent.Send> listener = new Listener<>(event -> {
        if(!client.getValBoolean()) return;

        String message = "Client -> " + event.getPacket().getClass().getName();

        if(values.getValBoolean()) for(Field field : event.getPacket().getClass().getDeclaredFields()) message += " " + field.getName() + "[" + field.toString() + "]";

        ChatUtility.cleanMessage(message);
    });

    @EventHandler
    private final Listener<PacketEvent.Receive> listener1 = new Listener<>(event -> {
        if(!server.getValBoolean()) return;

        String message = "Server -> " + event.getPacket().getClass().getName();

        if(values.getValBoolean()) for(Field field : event.getPacket().getClass().getDeclaredFields()) message += " " + field.getName() + "[" + field + "]";

        ChatUtility.cleanMessage(message);
    });
}
