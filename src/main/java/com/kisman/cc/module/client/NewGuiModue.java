package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;

public class NewGuiModue extends Module {
    public NewGuiModue() {
        super("NewGui", "54", Category.CLIENT);
    }

    public void onEnable() {
        super.onEnable();
        mc.displayGuiScreen(Kisman.instance.newGui);
        this.setToggled(false);
    }
}
