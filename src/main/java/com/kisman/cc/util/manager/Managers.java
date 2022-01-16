package com.kisman.cc.util.manager;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.util.render.PulseManager;
import me.zero.alpine.listener.*;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

import java.util.concurrent.atomic.AtomicLong;

public class Managers {
    public static Managers instance;

    public FPSManager fpsManager;
    public PulseManager pulseManager;
    public TimerManager timerManager;

    public AtomicLong lagTimer = new AtomicLong();

    public Managers() {
        instance = this;
    }

    public String getRainbowCommandMessage() {
        StringBuilder stringBuilder = new StringBuilder("[" + Kisman.NAME + "]");
        stringBuilder.insert(0, "\u00a7+");
        stringBuilder.append("\u00a7r");
        return stringBuilder.toString();
    }

    public void init() {
        fpsManager = new FPSManager();
        pulseManager = new PulseManager();
        timerManager = new TimerManager();

        Kisman.EVENT_BUS.subscribe(listener);
    }

    @EventHandler private final Listener<PacketEvent.Receive> listener = new Listener<>(event -> {if(event.getPacket() instanceof SPacketPlayerPosLook) lagTimer.set(System.currentTimeMillis());});

    public boolean passed(int ms) {return System.currentTimeMillis() - lagTimer.get() >= ms;}
    public void reset() {lagTimer.set(System.currentTimeMillis());}
    public long getTimeStamp() {return lagTimer.get();}
}
