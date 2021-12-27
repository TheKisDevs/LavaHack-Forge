package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;

import java.util.Arrays;

public class CSGOGui extends Module {
    public static CSGOGui instance;

    private Setting gui = new Setting("Gui", this, "New", Arrays.asList("Old", "New"));
    public Setting customSize = new Setting("CustomFontSize", this, false);

    public CSGOGui() {
        super("CSGOGui", "CSGOGui", Category.CLIENT);

        instance = this;

        setmgr.rSetting(gui);
        setmgr.rSetting(customSize);
    }

    public void onEnable() {
        mc.displayGuiScreen(gui.getValString().equalsIgnoreCase("Old") ? Kisman.instance.newGui : Kisman.instance.clickGuiNew);
        this.setToggled(false);
    }
}
