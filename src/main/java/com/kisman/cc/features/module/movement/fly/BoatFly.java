package com.kisman.cc.features.module.movement.fly;

import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.movement.MovementUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;

@ModuleInfo(
        name = "BoatFly",
        display = "Boat",
        submodule = true
)
public class BoatFly extends Module {
    private final Setting speed = register(new Setting("Speed", this, 2, 0, 10, true));
    private final Setting verticalSpeed = register(new Setting("Vertical Speed", this, 1, 0, 10, true));
    private final Setting downKey = register(new Setting("Down Key", this, Keyboard.KEY_LCONTROL).setVisible(verticalSpeed.getValInt() != 0));
    private final Setting glideSpeed = register(new Setting("Glide Speed", this, 0, -10, 10, true));
    private final Setting staticY = register(new Setting("Static Y", this, true));
    private final Setting hover = register(new Setting("Hover", this, false));
    private final Setting bypass = register(new Setting("Bypass", this, false));
    private final Setting extraCalc = register(new Setting("Extra Calc", this, false));

    public BoatFly() {
        super.setDisplayInfo(() -> "[" + speed.getValInt() + "]");
    }

    public void update() {
        if(mc.player == null || mc.world == null || mc.player.ridingEntity == null) return;
        Entity e = mc.player.ridingEntity;
        if (mc.gameSettings.keyBindJump.isKeyDown()) e.motionY = verticalSpeed.getValDouble();
        else if (!downKey.isNoneKey() && Keyboard.isKeyDown(downKey.getKey())) e.motionY = -verticalSpeed.getValDouble();
        else if(staticY.getValBoolean()) e.motionY = 0;
        else e.motionY = hover.getValBoolean() && mc.player.ticksExisted % 2 == 0 ? glideSpeed.getValDouble() : -glideSpeed.getValDouble();
        if (MovementUtil.isMoving()) {
            if(!extraCalc.getValBoolean()) {
                double[] motions = MovementUtil.strafe(speed.getValDouble());
                e.motionX = motions[0];
                e.motionZ = motions[1];
            } else {
                float dir = MovementUtil.getDirection();
                mc.player.motionX -= (MathHelper.sin(dir) * speed.getValFloat());
                mc.player.motionZ += (MathHelper.cos(dir) * speed.getValFloat());
            }
        } else {
            e.motionX = 0;
            e.motionZ = 0;
        }
        if (bypass.getValBoolean() && mc.player.ticksExisted % 4 == 0) if (mc.player.ridingEntity instanceof EntityBoat) mc.playerController.interactWithEntity(mc.player, mc.player.ridingEntity, EnumHand.MAIN_HAND);
    }
}
