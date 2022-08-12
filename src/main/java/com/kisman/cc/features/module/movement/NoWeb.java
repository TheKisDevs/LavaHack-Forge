package com.kisman.cc.features.module.movement;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import org.lwjgl.input.Keyboard;

public class NoWeb extends Module {
//    public Setting disableBB = register(new Setting("Add BB", this, true));
    public Setting onGround = register(new Setting("OnGround", this, true));
//    public Setting bbOffset = register(new Setting("BB Offset", this, 0, -2, 2, false));
    public Setting motionX = register(new Setting("MotionX", this, 0.84, -1, 5, false));
    public Setting motionY = register(new Setting("MotionY", this, 1, 0, 20, false));

    public NoWeb() {
        super("NoWeb", "", Category.MOVEMENT);
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;

        if (mc.player.isInWeb) {
            if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
                mc.player.isInWeb = true;
                mc.player.motionY *= motionY.getValDouble();
            } else if (onGround.getValBoolean()) mc.player.onGround = false;

            if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.keyCode) || Keyboard.isKeyDown(mc.gameSettings.keyBindBack.keyCode) || Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.keyCode)
                    || Keyboard.isKeyDown(mc.gameSettings.keyBindRight.keyCode)) {
                mc.player.isInWeb = false;
                mc.player.motionX *= motionX.getValDouble();
                mc.player.motionZ *= motionX.getValDouble();
            }
        }
    }
}
