package com.kisman.cc.features.module.Debug;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.PacketEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import org.cubic.m2l.M2L;
import org.cubic.m2l.M2LTarget;

public class M2LTest extends Module {

    private final M2L m2L;

    public M2LTest() {
        super("M2LTest", Category.DEBUG);
        this.m2L = new M2L(Kisman.EVENT_BUS);
        this.m2L.register(this);
    }

    @M2LTarget
    private void onPackSend(PacketEvent.Send event){
        if(!this.isToggled())
            return;
        if(event.getPacket() instanceof CPacketPlayerDigging)
            ChatUtility.info().printClientModuleMessage("Digging");
    }
}
