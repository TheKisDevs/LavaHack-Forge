package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;

public class Console extends Module{
    public Console() {
        super("Console", "da", Category.CLIENT);

        Kisman.instance.settingsManager.rSetting(new Setting("CmdPrompt", this, false));
    }

    public void onEnable() {
        super.onEnable();
        mc.displayGuiScreen(Kisman.instance.guiConsole);
        this.setToggled(false);
    }
}
