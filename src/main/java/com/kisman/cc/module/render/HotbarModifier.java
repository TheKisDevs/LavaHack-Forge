package com.kisman.cc.module.render;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;

public class HotbarModifier extends Module {
    public Setting containerShadow = new Setting("Shadow", this, false);
    public Setting selectedAstolfo = new Setting("Selected Slot Astolfo", this, true);

    public static HotbarModifier instance;

    public HotbarModifier() {
        super("HotbarModifier", Category.RENDER);

        instance = this;

        setmgr.rSetting(containerShadow);
    }
}
