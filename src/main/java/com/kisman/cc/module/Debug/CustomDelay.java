package com.kisman.cc.module.Debug;

import com.kisman.cc.event.events.client.settings.EventSettingChange;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import me.zero.alpine.listener.Listener;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CustomDelay extends Module {

    private final Setting delayMS = register(new Setting("DelayMS", this, 50, 0, 1000, true));
    private final Setting corePoolSize = register(new Setting("CorePoolSize", this, 1, 1, 16, true));

    public CustomDelay(){
        super("CustomDelay", Category.DEBUG);
    }

    private ScheduledExecutorService service;

    @Override
    public void onEnable(){
        this.service = new ScheduledThreadPoolExecutor(corePoolSize.getValInt());
        service.scheduleWithFixedDelay(this::onDelay, 0, delayMS.getValInt(), TimeUnit.MILLISECONDS);
    }

    private final Listener<EventSettingChange> listener = new Listener<>(event -> {
        if(event.setting != delayMS)
            return;
        service.shutdown();
        service = new ScheduledThreadPoolExecutor(1);
        service.scheduleWithFixedDelay(this::onDelay, 0, event.setting.getValInt(), TimeUnit.MILLISECONDS);
    });

    public void onDelay(){
        if(mc.player == null || mc.world == null)
            return;
        ChatUtility.message().printClientModuleMessage(mc.renderPartialTicksPaused + "");
    }

    public void onDisable(){
        service.shutdown();
    }
}
