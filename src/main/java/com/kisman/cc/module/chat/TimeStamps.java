package com.kisman.cc.module.chat;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.Event;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.TextUtil;
import com.kisman.cc.util.manager.Managers;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.TextComponentString;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeStamps extends Module {
    private Setting color = new Setting("Color", this, TextUtil.Color.GRAY);

    public TimeStamps() {
        super("TimeStamps", "TimeStamps in your chat", Category.CHAT);

        setmgr.rSetting(color);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
    }

    @EventHandler
    private final Listener<PacketEvent> listener = new Listener<>(event -> {
        if (event.getEra().equals(Event.Era.PRE) && color.getValEnum() != TextUtil.Color.NONE && event.getPacket() instanceof SPacketChat) {
            if (!((SPacketChat) event.getPacket()).isSystem()) {
                return;
            }
            String oldMessage = ((SPacketChat) event.getPacket()).chatComponent.getFormattedText();
            String message = getTimeString() + oldMessage;
            ((SPacketChat) event.getPacket()).chatComponent = new TextComponentString(message);
        }
    });

    public String getTimeString() {
        String date = new SimpleDateFormat("k:mm").format(new Date());

        return TextUtil.coloredString(date, (TextUtil.Color) color.getValEnum()) + "\u00a7r";
    }
}
