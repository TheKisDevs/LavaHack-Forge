package com.kisman.cc.friend;

import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;

public class FriendManager {
    public static FriendManager instance;

    private ArrayList<String> friendsName;

    public FriendManager() {
        friendsName = new ArrayList<>();

        instance = this;
    }

    public ArrayList<String> getFriends() {
        return friendsName;
    }

    public void addFriend(String name) {
        if(!friendsName.contains(name)) {
            friendsName.add(name);
        }
    }

    public void removeFriend(String name) {
        if(!friendsName.isEmpty()) {
            if (friendsName.contains(name)) {
                friendsName.remove(name);
            }
        }
    }

    public String getFriendsNames() {
        String str = "";

        for(String friend : friendsName) {
            if(friend.isEmpty()) continue;

            str += friend + ", ";
        }

        return str;
    }

    public boolean isFriend(EntityPlayer player) {
        return friendsName.contains(player.getName());
    }

    public boolean isFriend(String name) {
        return friendsName.contains(name);
    }
}
