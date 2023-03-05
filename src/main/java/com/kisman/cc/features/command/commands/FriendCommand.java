package com.kisman.cc.features.command.commands;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.command.Command;
import com.kisman.cc.util.manager.friend.FriendManager;

import java.util.ArrayList;

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

                ArrayList<String> friends = new ArrayList<>(FriendManager.instance.getFriends());

                for(int i = 0; i < friends.size(); i++) {
                    output += friends.get(i);
                    if(i != friends.size() - 1) output += ", ";
                }

                complete(output);
            } else throw new Exception();
        } catch (Exception e) {error("Usage: " + getSyntax());}
    }

    public String getDescription() {return "friend's command";}
    public String getSyntax() {return "friend add/remove <player's name> | friend list";}
}
