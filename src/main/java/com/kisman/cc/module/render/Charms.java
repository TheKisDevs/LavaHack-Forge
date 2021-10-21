package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;

import java.util.ArrayList;
import java.util.Arrays;

public class Charms extends Module {
    public Setting targetRender = new Setting("TargetRender", this, true);

    public static Charms instance;

    public Charms() {
        super("Charms", "Charms", Category.RENDER);

        instance = this;

        Kisman.instance.settingsManager.rSetting(new Setting("Texture", this, false));
        Kisman.instance.settingsManager.rSetting(new Setting("Render", this, false));

        setmgr.rSetting(targetRender);
    }

    public void onEnable() {
        if(Kisman.instance.moduleManager.getModule("KismanESP").isToggled()) {
            Kisman.instance.moduleManager.getModule("KismanESP").setToggled(false);
        }
    }
}
