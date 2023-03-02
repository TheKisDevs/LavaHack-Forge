package com.kisman.cc.util.manager;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

import java.util.concurrent.atomic.AtomicLong;

public class Managers {
    public static Managers instance;

    public CPSManager cpsManager;


    public AtomicLong lagTimer = new AtomicLong();

    public Managers() {
        instance = this;
    }

    public void init() {
        cpsManager = new CPSManager();

        Kisman.EVENT_BUS.subscribe(listener);
    }

    @EventHandler private final Listener<PacketEvent.Receive> listener = new Listener<>(event -> {if(event.getPacket() instanceof SPacketPlayerPosLook) lagTimer.set(System.currentTimeMillis());});

    public boolean passed(int ms) {return System.currentTimeMillis() - lagTimer.get() >= ms;}
    public void reset() {lagTimer.set(System.currentTimeMillis());}
    public long getTimeStamp() {return lagTimer.get();}
}
