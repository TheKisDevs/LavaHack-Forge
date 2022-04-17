package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.client.settings.EventSettingChange;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.customfont.CustomFontUtilKt;
import me.zero.alpine.listener.*;

import java.util.*;

public class CustomFontModule extends Module {
    private final Setting antiAlias = new Setting("Anti Alias", this, true);
    private final Setting fractionMetrics = new Setting("Fraction Metrics", this, true);
    public Setting mode = new Setting("Mode", this, "Comfortaa", new ArrayList<>(Arrays.asList("Verdana", "Comfortaa", "Comfortaa Light", "Comfortaa Bold", "Consolas", "LexendDeca", "Futura", "SfUi")));
    public Setting bold = new Setting("Bold", this, false).setVisible(() -> mode.checkValString("Verdana"));
    public Setting italic = new Setting("Italic", this, false).setVisible(() -> mode.checkValString("Verdana"));

    public static boolean turnOn = false;

    public static CustomFontModule instance;

    public CustomFontModule() {
        super("CustomFont", "custom font", Category.CLIENT);
        super.setDisplayInfo(() -> "[" + mode.getValString() + "]");

        instance = this;

        setmgr.rSetting(antiAlias);
        setmgr.rSetting(fractionMetrics);

        setmgr.rSetting(mode);
        setmgr.rSetting(bold);
        setmgr.rSetting(italic);
    }

    @EventHandler
    private final Listener<EventSettingChange.BooleanSetting> change = new Listener<>(event -> {
        if(event.action.equals(EventSettingChange.Action.Default)) if(event.setting.equals(antiAlias) || event.setting.equals(fractionMetrics)) CustomFontUtilKt.Companion.setAntiAliasAndFractionalMetrics(antiAlias.getValBoolean(), fractionMetrics.getValBoolean());
    });

    public void update() {
        turnOn = true;
    }
    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(change);
    }
    public void onDisable(){
        turnOn = false;
        try {
            Kisman.EVENT_BUS.unsubscribe(change);
        } catch (Exception ignored) {}
    }
}
