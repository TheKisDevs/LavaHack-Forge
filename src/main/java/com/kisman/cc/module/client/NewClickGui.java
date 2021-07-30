package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import org.lwjgl.input.Keyboard;

public class NewClickGui extends Module {

    public NewClickGui() {
        super("NewClickGui", "NewClickGui", Category.CLIENT);
        this.setKey(Keyboard.KEY_RSHIFT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        mc.displayGuiScreen(Kisman.instance.clickGUI);
        this.setToggled(false);
    }
}
