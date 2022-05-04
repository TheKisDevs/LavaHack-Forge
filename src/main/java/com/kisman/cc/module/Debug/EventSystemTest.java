package com.kisman.cc.module.Debug;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
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
