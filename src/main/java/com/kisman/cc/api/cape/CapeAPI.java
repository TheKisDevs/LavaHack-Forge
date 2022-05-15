package com.kisman.cc.api.cape;

import com.kisman.cc.api.util.URLReader;
import com.kisman.cc.api.util.exception.URLReaderException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CapeAPI {
    public URLReader reader;

    public List<UUID> uuids = new ArrayList<>();

    public static final String URL = "https://raw.githubusercontent.com/TheKisDevs/LavaHack-Assets/main/Capes.txt";

    public CapeAPI() {
        try {
            reader = new URLReader(URL);
            for(String uuid : reader.get()) {
                uuids.add(UUID.fromString(uuid));
            }
        } catch (URLReaderException ignored) {}
    }

    public boolean is(UUID uuid) {
        return uuids.contains(uuid);
    }
}