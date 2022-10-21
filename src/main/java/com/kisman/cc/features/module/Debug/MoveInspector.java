package com.kisman.cc.features.module.Debug;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventPlayerMove;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

public class MoveInspector extends Module {

    public MoveInspector(){
        super("MoveInspector", Category.DEBUG);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if(mc.player == null || mc.world == null){
            toggle();
            return;
        }
        Kisman.EVENT_BUS.subscribe(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(this);
    }

    @EventHandler
    private final Listener<EventPlayerMove> listener = new Listener<>(event -> {
        ChatUtility.info().printClientModuleMessage("MoverType: " + event.type.toString() + ", x: " + event.x + ", y: " + event.y + ", z: " + event.z);
    });
}
