package com.kisman.cc.module.movement;

import com.kisman.cc.mixin.mixins.accessor.AccessorEntityPlayer;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.util.MovementUtil;

public class NoSlowSneak extends Module {
    public NoSlowSneak() {
        super("NoSlowSneak", "NoSlowSneak", Category.MOVEMENT);
    }

    public void update() {
        if(mc.player == null || mc.world == null) return;

        if(mc.player.isSneaking()) {
            if(mc.gameSettings.keyBindForward.isKeyDown()) {
                mc.player.jumpMovementFactor = 0.1f;

                if(mc.player.onGround) {
                    mc.player.motionX *= 5;
                    mc.player.motionZ *= 5;
                    mc.player.motionX /= 3.1495;
                    mc.player.motionZ /= 3.1495;
                    MovementUtil.strafe(0.1245f);

                    if(mc.gameSettings.keyBindBack.isKeyDown()) {
                        mc.player.jumpMovementFactor = 0.08f;

                        if(mc.player.onGround) {
                            mc.player.motionX *= -5;
                            mc.player.motionZ *= -5;
                            mc.player.motionX /= -3.1495;
                            mc.player.motionZ /= -3.1495;
                            MovementUtil.strafe(0.1245f);
                        }
                    }
                }
            }
        } else {
            mc.player.jumpMovementFactor = 0.02f;
            ((AccessorEntityPlayer) mc.player).setSpeedInAir(0.02f);
        }
    }
}
