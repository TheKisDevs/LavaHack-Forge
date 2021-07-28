package com.kisman.cc.module.movement;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import net.minecraft.client.Minecraft;

public class AutoJump extends Module {
    public AutoJump() {
        super("AutoJump", "Automatic jump", Category.MOVEMENT);
    }

    public void update() {
        Minecraft.getMinecraft().gameSettings.autoJump = true;
    }

    public void onDisable() {
        Minecraft.getMinecraft().gameSettings.autoJump = false;
    }
}
