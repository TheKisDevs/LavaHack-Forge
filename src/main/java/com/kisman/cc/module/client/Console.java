package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;

public class Console extends Module{
    private final Setting theme = new Setting("Theme", this, Mode.Old);

    public Console() {
        super("Console", "da", Category.CLIENT);

        setmgr.rSetting(theme);
    }

    public void onEnable() {
        if(mc.world == null) return;
        mc.displayGuiScreen(theme.getValEnum().equals(Mode.Old) ? Kisman.instance.guiConsole : Kisman.instance.consoleGui);
        this.setToggled(false);
    }

    public enum Mode {
        Old, New
    }
}
