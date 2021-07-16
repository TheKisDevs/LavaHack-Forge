package com.kisman.cc.module.modules.movement;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class Step extends Module {
    public Step() {
        super("Step", Keyboard.KEY_G, Category.MOVEMENT);
    }

    public void update() {
        Minecraft.getMinecraft().player.stepHeight = 2.5f;
    }

    public void onDisable() {
        Minecraft.getMinecraft().player.stepHeight = 0.5f;
    }
}
