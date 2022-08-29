package com.kisman.cc.util.minecraft;

import com.kisman.cc.Kisman;
import com.mojang.authlib.GameProfile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author Cubic
 * @since 29.08.2022
 */
public class GameProfiles {

    public static final String URL_STRING = "https://api.mojang.com/users/profiles/minecraft/";

    public static final URL URL;

    static {
        try {
            URL = new URL(URL_STRING);
        } catch (MalformedURLException ingored){
            // this should never happen
            throw new IllegalStateException("Impossible state reached");
        }
    }

    public static GameProfile getGameProfile(String player){
        UUID uuid = getUUID(player);
        if(uuid == null)
            return null;
        return new GameProfile(uuid, player);
    }

    public static UUID getUUID(String player){
        String uuid = getUUIDString(player);
        if(uuid == null)
            return null;
        return UUID.fromString(uuid);
    }

    public static String getUUIDString(String player){
        String data = fetch(player);
        if(data == null)
            return null;
        return data.substring(data.indexOf("id") + 5, data.lastIndexOf('"'));
    }

    public static String fetch(String player){
        byte[] bytes = fetchRaw(player);
        if(bytes == null)
            return null;
        return new String(bytes);
    }

    public static byte[] fetchRaw(String player){
        java.net.URL playerURL;
        try {
            playerURL = new URL(URL_STRING + player);
        } catch (MalformedURLException e){
            Kisman.LOGGER.error("[GameProfiles]: Malformed URL", e);
            return null;
        }
        InputStream inputStream;
        try {
            inputStream = playerURL.openStream();
        } catch (IOException e){
            Kisman.LOGGER.error("[GameProfiles]: Could not open the url stream", e);
            return null;
        }
        byte[] buf = new byte[1024];
        int size = 0;
        int n;
        try {
            while((n = inputStream.read(buf)) >= 0)
                size += n;
        } catch (IOException e){
            Kisman.LOGGER.error("[GameProfiles]: Could not read bytes from URL", e);
            return null;
        }
        return Arrays.copyOf(buf, size);
    }
}
