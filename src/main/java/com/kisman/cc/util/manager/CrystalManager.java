package com.kisman.cc.util.manager;

import java.util.ArrayList;

import com.kisman.cc.util.Globals;

import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;

public class CrystalManager implements Globals {
    private long lastTime = 0l;
    private ArrayList<Long> timings = new ArrayList<>(); 

    public void place(CPacketPlayerTryUseItemOnBlock packet, long time) {
        if(lastTime == 0l) lastTime = time; 
        timings.add(time - lastTime);
    }

    public float getCrystalsPerSecond() {
        return Math.round(1000l / getAverageTimeForPlace());
    }

    public float getAverageTimeForPlace() {
        if(timings.isEmpty()) return 0;
        int counter = 0;
        long output = 0;
        for(long timing : timings) {
            output += timing;
            counter++;
        }

        return output / counter;
    }

    public void update() {
        if(mc.player == null || mc.world == null) {
            timings.clear();
            lastTime = 0l;
        }
    }
}
