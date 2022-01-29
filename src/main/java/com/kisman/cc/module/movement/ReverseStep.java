package com.kisman.cc.module.movement;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;

public class ReverseStep extends Module {
    public static ReverseStep instance;

    public Setting height = new Setting("Height", this, 1.0, 0.5, 4, false);

    public ReverseStep() {
        super("ReverseStep", "", Category.MOVEMENT);

        setmgr.rSetting(height);
    }

    public void update() {
        if (mc.world == null || mc.player == null || mc.player.isInWater() || mc.player.isInLava() || mc.player.isOnLadder() || mc.gameSettings.keyBindJump.isKeyDown()) return;
        if (mc.player != null && mc.player.onGround && !mc.player.isInWater() && !mc.player.isOnLadder()) {
            for (double y = 0.0; y < height.getValDouble() + 0.5; y += 0.01) {
                if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -y, 0.0)).isEmpty()) {
                    mc.player.motionY = -10.0;
                    break;
                }
            }
        }
    }
}
