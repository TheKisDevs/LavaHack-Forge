package com.kisman.cc.features.module.player;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;

public class AutoRespawn extends Module {
    public AutoRespawn() {
        super("AutoRespawn", "Automatically respawn after death", Category.PLAYER);
    }

    public void update() {
        if (mc.player == null || mc.world == null) return;

        if (mc.player.getHealth() == 0.0f) {
            mc.player.respawnPlayer();
        }
    }
}
