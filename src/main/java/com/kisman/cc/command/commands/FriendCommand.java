package com.kisman.cc.command.commands;

import com.kisman.cc.Kisman;
import com.kisman.cc.command.Command;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;

public class FriendCommand extends Command {
    public String[] subCommands = new String[] {"add", "remove", "list"};
    private String regex1 = "[list]";

    public FriendCommand() {
        super("friend");
    }

    public void runCommand(String s, String[] args) {
        try {
            if(args[0] != null && args[1] != null) {
                if(args[0].equalsIgnoreCase(subCommands[0])) {
                    Kisman.instance.friendManager.addFriend(args[1]);
                    ChatUtils.complete(args[1] + " added in friends!");
                } else if(args[0].equalsIgnoreCase(subCommands[1])) {
                    Kisman.instance.friendManager.removeFriend(args[1]);
                    ChatUtils.complete(args[1] + " removed from friends :(");
                } else ChatUtils.error("Usage: " + getSyntax());
            } else if(args[0] != null && args[0].matches(regex1)) {
                ChatUtils.message("----------------------------------");
                ChatUtils.message("Friends:");
                ChatUtils.message(Kisman.instance.friendManager.getFriendsNames());
                ChatUtils.message("----------------------------------");
            }
        } catch (Exception e) {ChatUtils.error("Usage: " + getSyntax());}
    }

    public String getDescription() {
        return "friend's command";
    }

    public String getSyntax() {
        return "friend <add/remove> <player's name> or friend list";
    }
}
