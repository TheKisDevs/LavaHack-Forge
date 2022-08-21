package com.kisman.cc.util.manager.friend;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.client.friend.FriendEvent;
import com.kisman.cc.features.module.client.Config;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;

public class FriendManager {
    public static FriendManager instance;

    private ArrayList<String> friendsName;

    public FriendManager() {
        friendsName = new ArrayList<>();

        instance = this;
    }

    public ArrayList<String> getFriends() {return friendsName;}
    public void addFriend(String name) {
        if(!friendsName.contains(name.toLowerCase())) {
            friendsName.add(name.toLowerCase());
            FriendEvent event = new FriendEvent(name.toLowerCase(), FriendEvent.Type.Add);
            Kisman.EVENT_BUS.post(event);
        }
    }
    public void removeFriend(String name) {
        if(!friendsName.isEmpty() && friendsName.contains(name.toLowerCase())) {
            friendsName.remove(name.toLowerCase());
            FriendEvent event = new FriendEvent(name.toLowerCase(), FriendEvent.Type.Remove);
            Kisman.EVENT_BUS.post(event);
        }
    }
    public boolean isFriend(EntityPlayer player) {return friendsName.contains(player.getName().toLowerCase()) && Config.instance.friends.getValBoolean();}
    public boolean isFriend(String name) {return friendsName.contains(name.toLowerCase()) && Config.instance.friends.getValBoolean();}
    public void setFriendsList(ArrayList<String> list) {friendsName = list;}
}
