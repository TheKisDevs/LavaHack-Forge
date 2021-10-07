package com.kisman.cc.module.movement;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.entity.Entity;

public class TargetStrafe extends Module {
    private Setting range = new Setting("Range", this, 3.6f, 0.1f, 7, false);
    private Setting motion = new Setting("Motion", this, 0.2f, 0.01f, 2, false);
    private Setting dynamic = new Setting("DynamicSpeed", this, true);
    private Setting damageBoost = new Setting("DamageBoost", this, false);
    private Entity target;
    private float format = 0;
    private float strafe = 1;

    public TargetStrafe() {
        super("TargetStrafe", "TargetStrafe", Category.MOVEMENT);

        setmgr.rSetting(range);
        setmgr.rSetting(motion);
        setmgr.rSetting(dynamic);
        setmgr.rSetting(damageBoost);
    }
}
