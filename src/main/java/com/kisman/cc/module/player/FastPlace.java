package com.kisman.cc.module.player;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;

public class FastPlace extends Module {
    public static FastPlace instance;

    public FastPlace() {
        super("FastPlace", "FastPlace", Category.PLAYER);

        instance = this;
    }

    public void onEnable() {
        mc.rightClickDelayTimer = 1;
    }

    public void onDisable() {
        mc.rightClickDelayTimer = 6;
    }
}
