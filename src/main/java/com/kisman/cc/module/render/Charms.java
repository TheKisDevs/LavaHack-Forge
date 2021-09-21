package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;

public class Charms extends Module {
    public Charms() {
        super("Charms", "Charms", Category.RENDER);

        Kisman.instance.settingsManager.rSetting(new Setting("Texture", this, false));
    }

    public void onEnable() {
        if(Kisman.instance.moduleManager.getModule("KismanESP").isToggled()) {
            Kisman.instance.moduleManager.getModule("KismanESP").setToggled(false);
        }
    }
}
