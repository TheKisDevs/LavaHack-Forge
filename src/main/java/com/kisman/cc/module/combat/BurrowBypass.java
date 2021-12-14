package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.BlockUtil;
import com.kisman.cc.util.EntityUtil;
import i.gishreloaded.gishcode.utils.TimerUtils;
import net.minecraft.util.math.BlockPos;

public class BurrowBypass extends Module {
    private BlockPos pos;
    private int delay, placeDelay, stage, jumpdelay, toggleDelay;
    private boolean jump;

    private TimerUtils timer = new TimerUtils();

    public BurrowBypass() {
        super("BurrowBypass", "get bypass for cc", Category.COMBAT);
    }

    public void onEnable() {
        pos = mc.player.getPosition();

        placeDelay = 0;
        stage = 1;
        toggleDelay = 0;
        jumpdelay = 0;
        timer.reset();
        jump = false;
        Kisman.TICK_TIMER = 1;
        delay = 0;
    }

    public void onDisable() {
        placeDelay = 0;
        stage = 1;
        toggleDelay = 0;
        jumpdelay = 0;
        timer.reset();
        jump = false;
        Kisman.TICK_TIMER = 1;
        pos = null;
        delay = 0;
    }

    public void update() {
        if(stage == 1) {
            delay++;
            mc.gameSettings.keyBindJump.pressed = true;

            Kisman.TICK_TIMER = 30;

            if(delay >= 42) {
                stage = 2;
                delay = 0;
                Kisman.TICK_TIMER = 1;
                mc.gameSettings.keyBindJump.pressed = false;
            }
        }

        if(stage == 2) {
            Kisman.TICK_TIMER = 1;

            if(mc.player.onGround) mc.gameSettings.keyBindJump.pressed = true;

            BlockUtil.placeBlock(pos);
            placeDelay++;

            if(placeDelay >= 30) {
                stage = 3;
                placeDelay = 0;
                mc.gameSettings.keyBindJump.pressed = false;
                Kisman.TICK_TIMER = 1;
            }
        }

        if(stage == 3) {
            toggleDelay++;
            Kisman.TICK_TIMER = 30;
            mc.gameSettings.keyBindJump.pressed = true;

            if(toggleDelay >= 25) {
                mc.player.motionY -= 0.4;
                Kisman.TICK_TIMER = 1;
                mc.gameSettings.keyBindJump.pressed = false;
                setToggled(false);
            }
        }
    }
}
