package com.kisman.cc.features.command.commands;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.command.Command;
import com.kisman.cc.util.manager.friend.FriendManager;

public class FriendCommand extends Command {
    public FriendCommand() {
        super("friend");
    }

    public void runCommand(String s, String[] args) {
        try {
            if(args[0].equalsIgnoreCase("add")) {
                Kisman.instance.friendManager.addFriend(args[1]);
                complete(args[1] + " added in friends!");
            } else if(args[0].equalsIgnoreCase("remove")) {
                Kisman.instance.friendManager.removeFriend(args[1]);
                complete(args[1] + " removed from friends :(");
            } else if(args[0].equalsIgnoreCase("list")) {
                String output = "Friends: ";

                for(int i = 0; i < FriendManager.instance.getFriends().size(); i++) {
                    output += FriendManager.instance.getFriends().get(i);
                    if(i != FriendManager.instance.getFriends().size() - 1) output += ", ";
                }

                complete(output);
            } else throw new Exception();
        } catch (Exception e) {error("Usage: " + getSyntax());}
    }

    public String getDescription() {return "friend's command";}
    public String getSyntax() {return "friend add/remove <player's name> | friend list";}
}
