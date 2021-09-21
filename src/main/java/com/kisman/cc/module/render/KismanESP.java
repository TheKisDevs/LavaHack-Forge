package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;

public class KismanESP extends Module {
    public KismanESP() {
        super("KismanESP", "3", Category.RENDER);

        Kisman.instance.settingsManager.rSetting(new Setting("voidsetting", this, "void", "setting"));
    }

    public void onEnable() {
        if(Kisman.instance.moduleManager.getModule("Charms").isToggled()) {
            Kisman.instance.moduleManager.getModule("Charms").setToggled(false);
        }
    }
}
