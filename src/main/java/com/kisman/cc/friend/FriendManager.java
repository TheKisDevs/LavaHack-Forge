package com.kisman.cc.friend;

import com.kisman.cc.module.client.Config;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;

public class FriendManager {
    public static FriendManager instance;

    private final ArrayList<String> friendsName;

    public FriendManager() {
        friendsName = new ArrayList<>();

        instance = this;
    }

    public String getFriendsNames() {
        StringBuilder str = new StringBuilder();

        for(String friend : friendsName) {
            if(friend.isEmpty()) continue;

            str.append(friend).append("\n");
        }

        return str.toString();
    }

    public ArrayList<String> getFriends() {return friendsName;}
    public void addFriend(String name) {if(!friendsName.contains(name)) friendsName.add(name);}
    public void removeFriend(String name) {if(!friendsName.isEmpty() && friendsName.contains(name)) friendsName.remove(name);}
    public boolean isFriend(EntityPlayer player) {return friendsName.contains(player.getName()) && Config.instance.friends.getValBoolean();}
    public boolean isFriend(String name) {return friendsName.contains(name) && Config.instance.friends.getValBoolean();}
}
