package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;

public class VegaGui extends Module {

    private Setting particles_color = new Setting("Particles Color", this, "Particles Color", new float[] {0, 1, 0, 1}, false);

    public VegaGui() {
        super("VegaGui", "gui", Category.CLIENT);

        setmgr.rSetting(particles_color);
    }

    public void onEnable() {
        mc.displayGuiScreen(Kisman.instance.gui);
        this.setToggled(false);
    }
}
