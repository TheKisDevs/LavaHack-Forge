package com.kisman.cc.api.cape;

import com.kisman.cc.api.util.PasteBinAPI;

import java.util.UUID;

public class CapeAPI {
    public PasteBinAPI pasteBinAPI;

    public static final String URL = "https://pastebin.com/raw/Mjhz9nxW";

    public CapeAPI() {
        pasteBinAPI = new PasteBinAPI(URL);
    }

    public boolean is(UUID uuid) {
        for(String uuid_ : pasteBinAPI.get()) if(uuid_.equals(uuid.toString())) return true;
        return false;
    }
}
