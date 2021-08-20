package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.settings.Setting;
import org.lwjgl.input.Keyboard;

public class ClickGUI extends Module {
    public static boolean openGui = false;

    public ClickGUI() {
        super("ClickGUI", "ClickGUI", Category.CLIENT);
        this.setKey(Keyboard.KEY_U);
    }

    @Override
    public void onEnable() {
        openGui = true;
        super.onEnable();
        mc.displayGuiScreen(Kisman.instance.clickGui);
        this.setToggled(false);
    }

    public void onDisable() {
        openGui = false;
    }
}
