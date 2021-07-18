package com.kisman.cc.module.modules.movement;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.setting.settings.SettingDouble;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class Step extends Module {
   SettingDouble height;

    public Step() {
        super("Step", Keyboard.KEY_G, Category.MOVEMENT);
        height = this.register("height", 2.5, 0.5, 2.5);
    }

    public void update() {
        Minecraft.getMinecraft().player.stepHeight = (float) height.getValue();
    }

    public void onDisable() {
        Minecraft.getMinecraft().player.stepHeight = 0.5f;
    }
}
