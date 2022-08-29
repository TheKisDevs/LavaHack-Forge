package com.kisman.cc.features.command.commands;

import com.kisman.cc.features.command.Command;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public class IsOnline extends Command {

    public IsOnline() {
        super("isonline");
    }

    @Override
    public void runCommand(@NotNull String s, @NotNull String[] args) {
        if(args.length < 1)
            return;
        String player = args[0];
        MinecraftServer server = mc.player.getServer();
        if(server == null || mc.isSingleplayer()){
            ChatUtility.info().printClientClassMessage("You are not in a server right now");
            return;
        }
        if(server.getPlayerProfileCache().getGameProfileForUsername(player) == null)
            notOnline(player);
        else
            online(player);
    }

    private void notOnline(String player){
        ChatUtility.info().printClientClassMessage(player + " is not online.");
    }

    private void online(String player){
        ChatUtility.info().printClientClassMessage(player + " is online.");
    }

    @Override
    public String getDescription() {
        return "checked wether a player is online or not";
    }

    @Override
    public String getSyntax() {
        return "isonline";
    }
}
