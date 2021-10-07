package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.settings.Setting;
import org.lwjgl.input.Keyboard;

public class ClickGUI extends Module {
    public static ClickGUI instance;

    public Setting scrollSpeed = new Setting("ScrollSpeed", this, 15, 1, 100, true);

    public ClickGUI() {
        super("ClickGUI", "ClickGUI", Category.CLIENT);
        this.setKey(Keyboard.KEY_U);

        instance = this;

        setmgr.rSetting(scrollSpeed);

        Kisman.instance.settingsManager.rSetting(new Setting("TestButton", this, false));
    }

    @Override
    public void onEnable() {
        super.onEnable();
        mc.displayGuiScreen(Kisman.instance.clickGui);
        this.setToggled(false);
    }
}
