package com.kisman.cc.module.combat;

import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

public class AimAssist extends Module {
    private final Setting range = new Setting("Range", this, 4.5f, 1, 6, false);
    private final Setting speed = new Setting("Speed", this, 0.5f, 0.1f, 2, false);

    private EntityPlayer target;

    public AimAssist() {
        super("AimAssist", Category.COMBAT);

        setmgr.rSetting(range);
        setmgr.rSetting(speed);
    }

    public void update() {
        target = EntityUtil.getTarget(range.getValFloat());

        if(target == null) return;
        else super.setDisplayInfo("[" + target.getName() + TextFormatting.GRAY + "]");

        float[] rotsToTarget = RotationUtils.getRotation(target);

        mc.player.rotationYaw = (float) AnimationUtils.animate( rotsToTarget[0], mc.player.rotationYaw, speed.getValFloat());
        mc.player.rotationPitch = (float) AnimationUtils.animate( rotsToTarget[1], mc.player.rotationPitch, speed.getValFloat());
    }
}
