package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;

public class CSGOGui extends Module {
    public CSGOGui() {
        super("CSGOGui", "CSGOGui", Category.CLIENT);
    }

    public void onEnable() {
        mc.displayGuiScreen(Kisman.instance.newGui);
        this.setToggled(false);
    }
}
