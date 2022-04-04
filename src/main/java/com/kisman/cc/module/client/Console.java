package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;

public class Console extends Module{
    public Console() {
        super("Console", "da", Category.CLIENT);
    }

    public void onEnable() {
        if(mc.world == null) return;
        mc.displayGuiScreen(Kisman.instance.guiConsole);
        this.setToggled(false);
    }
}
