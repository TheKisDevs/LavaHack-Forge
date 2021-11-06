package com.kisman.cc.module.client;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;

public class Cape extends Module {
    public static Cape instance;

    public Setting gif = new Setting("Gif", this, false);

    public Cape() {
        super("Cape", "Custom cape", Category.CLIENT);

        instance = this;

        setmgr.rSetting(gif);
    }
}
