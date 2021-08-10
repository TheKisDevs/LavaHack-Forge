package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.hud.hudgui.HudGui;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;

public class HUDGui extends Module {
    HudGui hudGui = new HudGui();
    public static boolean enable = false;

    public HUDGui() {
        super("HUDGui", "", Category.CLIENT);
    }

    @Override
    public void onEnable() {
        enable = true;
        super.onEnable();
        mc.displayGuiScreen(Kisman.instance.hudGui);
        this.setToggled(false);
    }

    public void onDisable() {
        enable = false;
    }
}
