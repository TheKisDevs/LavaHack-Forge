package com.kisman.cc.features.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventCape;
import com.kisman.cc.features.capes.CapeManager;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.util.enums.CapeEnum;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;

public class Cape extends Module {
    public static Cape instance;

    public SettingEnum<CapeEnum> mode = new SettingEnum<>("Cape Mode", this, CapeEnum.Gradient).register();

    public Cape() {
        super("Cape", "Custom cape", Category.CLIENT);
        super.setDisplayInfo(() -> "[" + mode.getValString() + "]");

        instance = this;
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(cape);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(cape);
    }

    @EventHandler private final Listener<EventCape> cape = new Listener<>(event -> {
        if(/*event.getInfo() == mc.player.getPlayerInfo() || */CapeManager.INSTANCE.has(event.getInfo().getGameProfile().getId().toString())) {
            event.setResLoc(mode.getValEnum().location());
        }
    });
}
