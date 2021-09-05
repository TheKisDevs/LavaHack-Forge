package com.kisman.cc.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.client.Minecraft;

public class AutoJump extends Module {
    public AutoJump() {
        super("AutoJump", "Automatic jump", Category.MOVEMENT);

        Kisman.instance.settingsManager.rSetting(new Setting("voidsetting", this, "void", "setting"));
    }

    public void update() {
        if(mc.player.onGround) {
            mc.player.jump();
        }
    }
}
