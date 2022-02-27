package com.kisman.cc.module.render;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;

public class HotbarModifier extends Module {
    public Setting containerShadow = new Setting("Shadow", this, false);
    public Setting primaryAstolfo = new Setting("Primary Astolfo", this, true);
    public Setting offhand = new Setting("Offhand", this, true);
    public Setting offhandGradient = new Setting("Offhand Gradient", this, false);

    public static HotbarModifier instance;

    public HotbarModifier() {
        super("HotbarModifier", Category.RENDER);

        instance = this;

        setmgr.rSetting(containerShadow);
        setmgr.rSetting(primaryAstolfo);
        setmgr.rSetting(offhand);
        setmgr.rSetting(offhandGradient);
    }
}
