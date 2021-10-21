package com.kisman.cc.module.misc;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.text.ChatType;

public class NameProtect extends Module {
    private Setting name = new Setting("Name", this, "Kisman", "Kisman", true);

    public static NameProtect instance;

    public NameProtect() {
        super("NameProtect", "NameProtect", Category.MISC);

        instance = this;

        setmgr.rSetting(name);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
    }

    @EventHandler
    private final Listener<PacketEvent.Receive> listener = new Listener<>(event -> {
        SPacketChat packet;
        if (event.getPacket() instanceof SPacketChat && (packet = (SPacketChat)event.getPacket()).getType() != ChatType.GAME_INFO && this.getChatNames(packet.getChatComponent().getFormattedText(), packet.getChatComponent().getUnformattedText())) {
            event.cancel();
        }
    });

    private boolean getChatNames(String message, String unformatted) {
        String out = message;
        if (NameProtect.mc.player == null) {
            return false;
        }
        out = out.replace(mc.player.getName(), name.getValString());
        ChatUtils.simpleMessage(out);
        return true;
    }

    public static String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn) {
        String dname = networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName((Team)networkPlayerInfoIn.getPlayerTeam(), (String)networkPlayerInfoIn.getGameProfile().getName());
        dname = dname.replace(mc.player.getName(), NameProtect.instance.name.getValString());
        return dname;
    }
}
