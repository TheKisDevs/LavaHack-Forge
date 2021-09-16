package com.kisman.cc.module.movement;

import java.util.*;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.*;

import i.gishreloaded.gishcode.utils.Utils;
import i.gishreloaded.gishcode.wrappers.Wrapper;
import net.minecraft.util.math.MathHelper;

public class Speed extends Module{
    public Speed() {
        super("Speed", "SPID", Category.MOVEMENT);

        Kisman.instance.settingsManager.rSetting(new Setting("SpeedMode", this, "Strafe", new ArrayList<String>(Arrays.asList("Strafe", "OnGround"))));
    }

    public void update() {
        String mode = Kisman.instance.settingsManager.getSettingByName(this, "SpeedMode").getValString();

        boolean boost = Math.abs(Wrapper.INSTANCE.player().rotationYawHead - Wrapper.INSTANCE.player().rotationYaw) < 90;

        if(mc.player == null && mc.world == null) return;

        if(Wrapper.INSTANCE.player().moveForward > 0 && Wrapper.INSTANCE.player().hurtTime < 5) {
            if(mode.equalsIgnoreCase("OnGround") && mc.player.onGround) {
                mc.player.motionY = 0.405;
                float f = Utils.getDirection();

                mc.player.motionX -= (double)(MathHelper.sin(f) * 0.2F);
                mc.player.motionZ += (double)(MathHelper.cos(f) * 0.2F);
            } else if(mode.equalsIgnoreCase("OnGround")) {
                double currentSpeed = Math.sqrt(Wrapper.INSTANCE.player().motionX * Wrapper.INSTANCE.player().motionX + Wrapper.INSTANCE.player().motionZ * Wrapper.INSTANCE.player().motionZ);
                double speed = boost ? 1.0064 : 1.001;
  
                double direction = Utils.getDirection();
  
                Wrapper.INSTANCE.player().motionX = -Math.sin(direction) * speed * currentSpeed;
                Wrapper.INSTANCE.player().motionZ = Math.cos(direction) * speed * currentSpeed;
            }
        }
    }
}
