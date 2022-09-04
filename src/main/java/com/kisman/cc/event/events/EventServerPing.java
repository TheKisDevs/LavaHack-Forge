package com.kisman.cc.event.events;

import com.kisman.cc.event.Event;
import net.minecraft.client.multiplayer.ServerData;

public class EventServerPing extends Event {

    private final ServerData server;

    public EventServerPing(ServerData server) {
        this.server = server;
    }

    public ServerData getServer() {
        return server;
    }

    public static class Normal extends EventServerPing {

        public Normal(ServerData server) {
            super(server);
        }
    }

    public static class Compatibility extends EventServerPing {

        public Compatibility(ServerData server) {
            super(server);
        }
    }
}
