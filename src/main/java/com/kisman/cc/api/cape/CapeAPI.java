package com.kisman.cc.api.cape;

import com.kisman.cc.api.util.PasteBinAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CapeAPI {
    public PasteBinAPI pasteBinAPI;

    public List<String> uuids = new ArrayList<>();

    public static final String URL = "https://pastebin.com/raw/Mjhz9nxW";

    public CapeAPI() {
        pasteBinAPI = new PasteBinAPI(URL);
        uuids.addAll(pasteBinAPI.get());
    }

    public boolean is(UUID uuid) {
        return uuids.contains(uuid);
    }
}
