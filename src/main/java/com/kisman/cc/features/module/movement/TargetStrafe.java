package com.kisman.cc.features.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.subsystem.subsystems.EnemyManagerKt;
import com.kisman.cc.features.subsystem.subsystems.Target;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.world.WorldUtilKt;
import net.minecraft.entity.player.EntityPlayer;

public class TargetStrafe extends Module {
    private final Setting radius = register(new Setting("Radius", this, 3.6f, 0.1f, 7, false));
    private final Setting speed = register(new Setting("Speed", this, 3.19, 0.15f, 50, false));
    private final Setting autoJump = register(new Setting("Auto Jump", this, false));

    private int direction;

    @Target
    public EntityPlayer target;

    public TargetStrafe() {
        super("TargetStrafe", "TargetStrafe", Category.MOVEMENT);
        super.setDisplayInfo(() -> "[" + (target == null ? "no target no fun" : target.getName()) + "]");
    }

    public void update() {
        if(mc.player == null || mc.world == null || !Kisman.instance.moduleManager.getModule("KillAuraRewrite").isToggled()) return;

        target = EnemyManagerKt.nearest();

        if(target == null) return;

        if(mc.player.collidedHorizontally) direction = -direction;

        if(autoJump.getValBoolean() && mc.player.onGround) mc.player.jump();
        if(mc.gameSettings.keyBindLeft.isKeyDown()) direction = 1;
        if(mc.gameSettings.keyBindRight.isKeyDown()) direction = -1;

        mc.player.movementInput.moveForward = 0;

        float yaw = WorldUtilKt.rotation(target)[0];

        if (mc.player.getDistance(target) <= radius.getValDouble()) setSpeed(speed.getValInt() - (0.2 - this.speed.getValDouble() / 100.0), yaw, this.direction, 0.0);
        else setSpeed(speed.getValInt() - (0.2 - this.speed.getValDouble() / 100.0), yaw, this.direction, 1.0);
    }

    private void setSpeed(double d, float f, double d2, double d3) {
        double d4 = d3;
        double d5 = d2;
        float f2 = f;
        if (d4 == 0.0 && d5 == 0.0) {
            mc.player.motionZ = 0.0;
            mc.player.motionX = 0.0;
        } else {
            if (d4 != 0.0) {
                if (d5 > 0.0) f2 += ((d4 > 0.0) ? -45 : 45);
                else if (d5 < 0.0) f2 += ((d4 > 0.0) ? 45 : -45);
                d5 = 0.0;
                if (d4 > 0.0) d4 = 1.0;
                else if (d4 < 0.0) d4 = -1.0;
            }
            final double d6 = Math.cos(Math.toRadians(f2 + 90.0f));
            final double d7 = Math.sin(Math.toRadians(f2 + 90.0f));
            mc.player.motionX = d4 * d * d6 + d5 * d * d7;
            mc.player.motionZ = d4 * d * d7 - d5 * d * d6;
        }
    }
}
