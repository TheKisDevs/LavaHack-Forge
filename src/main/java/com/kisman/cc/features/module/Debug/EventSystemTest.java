package com.kisman.cc.features.module.Debug;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import org.cubic.eventsystem.Subscribe;

public class EventSystemTest extends Module {

    public EventSystemTest(){
        super("EventSystemTest", Category.DEBUG);
        //Kisman.instance.cubicEventBus.subscribe(this);
    }

    private long ticks = 0;

    @Subscribe
    public void onTestTickEvent(TestTickEvent event){
        if(mc.world == null || mc.player == null)
            return;

        ChatUtility.info().printClientModuleMessage("Ticks passed" + ticks++);
    }

    @Override
    public void update(){
        //Kisman.instance.cubicEventBus.post(new TestTickEvent());
    }

    private static final class TestTickEvent {

    }
}
