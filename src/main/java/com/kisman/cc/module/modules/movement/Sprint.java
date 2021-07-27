package com.kisman.cc.module.modules.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public class Sprint extends Module{
    public Sprint() {
        super("Sprint", "Auto sprint", Category.MOVEMENT);
    }

    public void update() {
        KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindSprint.getKeyCode(), true);
    }

    public void onDisable() {
        KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindSprint.getKeyCode(), false);
    }
}
