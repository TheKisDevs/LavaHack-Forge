package com.kisman.cc.module.chat;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;

public class Notification extends Module {
    public static Notification instance;

    private Setting autoCrystal = new Setting("AutoCrystal", this, "AutoCrystal");

    public Setting target = new Setting("Target", this, true);
    public Setting placeObby = new Setting("Obby", this, true);

    public Notification() {
        super("Notification", "kgdrklbdf", Category.CHAT);

        instance = this;

        setmgr.rSetting(autoCrystal);
        setmgr.rSetting(target);
        setmgr.rSetting(placeObby);
    }
}
