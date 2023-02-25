package com.kisman.cc.features.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.TimerUtils;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.CPacketPlayer;

import java.util.ArrayList;
import java.util.List;

public class Blink extends Module {

    private final Setting sendTime = register(new Setting("SendTime", this, 0, 0, 100, true));
    private final Setting fakePlayer = register(new Setting("FakePlayer", this, true));

    public Blink(){
        super("Blink", Category.MOVEMENT);
    }

    private final List<CPacketPlayer.Position> packets = new ArrayList<>();

    @Override
    public void onEnable() {
        if(mc.player == null || mc.world == null) return;
        if(fakePlayer.getValBoolean()){
            EntityOtherPlayerMP entity = new EntityOtherPlayerMP(mc.world, mc.player.getGameProfile());
            mc.world.addEntityToWorld(Integer.MAX_VALUE, entity);
        }
        Kisman.EVENT_BUS.subscribe(packetListener);
    }

    @Override
    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(packetListener);
        mc.world.removeEntityFromWorld(Integer.MAX_VALUE);
        Thread thread = new Thread(() -> {
            if(packets.size() == 0) return;
            int time = sendTime.getValInt() / packets.size();
            TimerUtils timer = new TimerUtils();
            for(CPacketPlayer.Position packet : packets){
                while(!timer.passedMillis(time));
                mc.player.connection.sendPacket(packet);
                timer.reset();
            }
        });
        thread.start();
    }

    private final Listener<PacketEvent.Send> packetListener = new Listener<>(event -> {
        if(!(event.getPacket() instanceof CPacketPlayer.Position)) return;
        CPacketPlayer.Position packet = (CPacketPlayer.Position) event.getPacket();
        packets.add(packet);
        event.cancel();
    });
}
