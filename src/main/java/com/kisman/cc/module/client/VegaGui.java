package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;

public class VegaGui extends Module {
    public static VegaGui instance;

    private Setting particles_color = new Setting("Particles Color", this, "Particles Color", new float[] {0, 1, 0, 1}, false);
    public Setting test = new Setting("Test Gui Update", this, false);

    public VegaGui() {
        super("VegaGui", "gui", Category.CLIENT);

        instance = this;

        setmgr.rSetting(particles_color);
        setmgr.rSetting(test);
    }

    public void onEnable() {
        mc.displayGuiScreen(Kisman.instance.gui);
        this.setToggled(false);
    }
}
