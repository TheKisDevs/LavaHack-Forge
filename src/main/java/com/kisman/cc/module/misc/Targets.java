package com.kisman.cc.module.misc;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;

public class Targets extends Module{
    public Targets() {
        super("Targets", "gay+++", Category.MISC);
        this.setToggled(true);

        Kisman.instance.settingsManager.rSetting(new Setting("Players", this, false));
        Kisman.instance.settingsManager.rSetting(new Setting("Mobs", this, false));
        Kisman.instance.settingsManager.rSetting(new Setting("Invisibles", this, false));
        Kisman.instance.settingsManager.rSetting(new Setting("Murder", this, false));
    }
}
