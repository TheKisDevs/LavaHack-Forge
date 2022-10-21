package com.kisman.cc.features.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventRenderAttackIndicator;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.WorkInProgress;
import me.zero.alpine.event.type.Cancellable;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

@WorkInProgress
public class NoCrosshair extends Module {

    public NoCrosshair(){
        super("NoCrosshair", Category.RENDER);
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
    private final Listener<EventRenderAttackIndicator> listener = new Listener<>(Cancellable::cancel);
}
