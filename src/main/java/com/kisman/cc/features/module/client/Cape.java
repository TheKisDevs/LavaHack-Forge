package com.kisman.cc.features.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventCape;
import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.TimerUtils;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;

public class Cape extends Module {
    public static Cape instance;

    public Setting mode = new Setting("Cape Mode", this, "Gif", Arrays.asList("Gif", "Xulu+", "GentleManMC", "Kuro", "Putin", "Gradient"));

    private int count = 0;
    private final TimerUtils timer = new TimerUtils();

    public Cape() {
        super("Cape", "Custom cape", Category.CLIENT);
        super.setDisplayInfo(() -> "[" + mode.getValString() + "]");

        instance = this;

        setmgr.rSetting(mode);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(cape);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(cape);
    }

    @EventHandler private final Listener<EventCape> cape = new Listener<>(event -> {
        if(event.getInfo() == Minecraft.getMinecraft().player.getPlayerInfo() || Kisman.instance.capeAPI.is(event.getInfo().getGameProfile().getId())) {
            switch(mode.getValString()) {
                case "Gif":
                    event.setResLoc(getCape());
                    break;
                case "Xulu+":
                    event.setResLoc(new ResourceLocation("kismancc:cape/xuluplus/xulupluscape.png"));
                    break;
                case "Kuro":
                    event.setResLoc(new ResourceLocation("kismancc:cape/kuro/kuro.png"));
                    break;
                case "GentleManMC":
                    event.setResLoc(new ResourceLocation("kismancc:cape/gentlemanmc/GentlemanMC.png"));
                    break;
                case "Putin":
                    event.setResLoc(new ResourceLocation("kismancc:cape/putin/putin.png"));
                    break;
                case "Gradient":
                    event.setResLoc(new ResourceLocation("kismancc:cape/gradient/gradient.png"));
                    break;
            }
        }
    });

    private ResourceLocation getCape() {
        if(count > 34) count = 0;

        final ResourceLocation cape = new ResourceLocation("kismancc:cape/rainbow/cape-" + count + ".png");

        if(timer.passedMillis(85)) {
            count++;
            timer.reset();
        }

        return cape;
    }
}
