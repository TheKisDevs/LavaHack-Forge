package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;

public class Console extends Module{
    public Console() {
        super("Console", "da", Category.CLIENT);
    }

    public void onEnable() {
        super.onEnable();
        mc.displayGuiScreen(Kisman.instance.guiConsole);
        this.setToggled(false);
    }
}
