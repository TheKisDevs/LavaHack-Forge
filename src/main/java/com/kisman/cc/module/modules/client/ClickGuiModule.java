package com.kisman.cc.module.modules.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class ClickGuiModule extends Module {
    public ClickGuiModule() {
        super("ClickGui", Keyboard.KEY_RSHIFT, Category.CLIENT);
    }

    public void onEnable() {
        Minecraft.getMinecraft().displayGuiScreen(Kisman.instance.clickGui);
        toggle();
    }
}
