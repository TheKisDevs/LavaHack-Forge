package com.kisman.cc.module.client;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;

public class Cape extends Module {
    public static Cape instance;

    public Setting mode = new Setting("Cape Mode", this, CapeMode.STATIC);

    public Cape() {
        super("Cape", "Custom cape", Category.CLIENT);

        instance = this;

        setmgr.rSetting(mode);
    }

    public enum CapeMode {
        STATIC,
        GIF,
        XULUplus
    }
}
