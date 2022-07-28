package com.kisman.cc.features.module.misc;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;

public class SelfDamage extends Module {
    private final Setting jump = register(new Setting("Jumps", this, 3, 3, 50, true));
    private final Setting timer = register(new Setting("JumpTimer", this, 3, 1, 1000, true));

    private int jumpCount;

    public SelfDamage() {
        super("SelfDamage", "SelfDamage", Category.MISC);
    }

    public void onEnable() {
        jumpCount = 0;
    }

    public void onDisable() {
        mc.timer.tickLength = 1;
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;
        if(jumpCount < jump.getValDouble()) {
            mc.timer.tickLength = timer.getValFloat();
            mc.player.onGround = false;
        }

        if(mc.player.onGround) {
            if(jumpCount < jump.getValDouble()) {
                mc.player.jump();
                jumpCount++;
            } else mc.timer.tickLength = 1;
        }
    }
}
