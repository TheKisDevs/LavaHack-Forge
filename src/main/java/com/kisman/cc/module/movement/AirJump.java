package com.kisman.cc.module.movement;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;

import java.util.ArrayList;
import java.util.Arrays;

public class AirJump extends Module {
    private Setting mode = new Setting("Mode", this, "Vanilla", new ArrayList<>(Arrays.asList("Vanilla", "NCP")));

    public AirJump() {
        super("AirJump", "Category", Category.MOVEMENT);

        setmgr.rSetting(mode);
    }

    public void update() {
        if (mode.getValString().equalsIgnoreCase("Vanilla")) {
            if (mc.gameSettings.keyBindJump.isPressed()) {
                mc.player.motionY = 0.7;
            }
        }
        if (mode.getValString().equalsIgnoreCase("NCP")) {
            mc.player.onGround = true;
            mc.player.isAirBorne = false;
        }
    }
}
