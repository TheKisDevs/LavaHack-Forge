package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;

public class NewGui extends Module {
    public NewGui() {
        super("NewGui", "gui", Category.CLIENT);
    }

    public void onEnable() {
        mc.displayGuiScreen(Kisman.instance.gui);
        this.setToggled(false);
    }
}
