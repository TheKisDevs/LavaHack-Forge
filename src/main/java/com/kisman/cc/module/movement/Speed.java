package com.kisman.cc.module.movement;

import java.util.*;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.*;

import com.kisman.cc.util.EntityUtil;
import com.kisman.cc.util.PlayerUtil;
import i.gishreloaded.gishcode.utils.Utils;
import i.gishreloaded.gishcode.wrappers.Wrapper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

public class Speed extends Module {
    private float yPortSpeed;

    private Setting speedMode = new Setting("SpeedMode", this, "Strafe", new ArrayList<>(Arrays.asList("Strafe", "YPort")));

    private Setting yPortLine = new Setting("YPortLine", this, "YPort");
    private Setting yWater = new Setting("Water", this, false);
    private Setting yLava = new Setting("Lava", this, false);

    public Speed() {
        super("Speed", "SPID", Category.MOVEMENT);
        super.setDisplayInfo("[" + speedMode.getValString() + TextFormatting.GRAY + "]");

        setmgr.rSetting(speedMode);

        Kisman.instance.settingsManager.rSetting(new Setting("YPortSpeed", this, 0.06f, 0.01f, 0.15f, false));

        setmgr.rSetting(yPortLine);
        setmgr.rSetting(yWater);
        setmgr.rSetting(yLava);
    }

    public void onDisable() {
        EntityUtil.resetTimer();
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        boolean boost = Math.abs(Wrapper.INSTANCE.player().rotationYawHead - Wrapper.INSTANCE.player().rotationYaw) < 90;

        this.yPortSpeed = (float) Kisman.instance.settingsManager.getSettingByName(this, "YPortSpeed").getValDouble();

        if(Wrapper.INSTANCE.player().moveForward > 0 && Wrapper.INSTANCE.player().hurtTime < 5 && speedMode.getValString().equalsIgnoreCase("Strafe")) {
            if(mc.player.onGround) {
                mc.player.motionY = 0.405;
                float f = Utils.getDirection();

                mc.player.motionX -= (double) (MathHelper.sin(f) * 0.2F);
                mc.player.motionZ += (double) (MathHelper.cos(f) * 0.2F);
            } else {
                double currentSpeed = Math.sqrt(Wrapper.INSTANCE.player().motionX * Wrapper.INSTANCE.player().motionX + Wrapper.INSTANCE.player().motionZ * Wrapper.INSTANCE.player().motionZ);
                double speed = boost ? 1.0064 : 1.001;
  
                double direction = Utils.getDirection();
  
                Wrapper.INSTANCE.player().motionX = -Math.sin(direction) * speed * currentSpeed;
                Wrapper.INSTANCE.player().motionZ = Math.cos(direction) * speed * currentSpeed;
            }
        }

        if(speedMode.getValString().equalsIgnoreCase("YPort")) {
            handleYPortSpeed();
        }
    }

    private void handleYPortSpeed() {
        if(!PlayerUtil.isMoving(mc.player) || (mc.player.isInWater() && !yWater.getValBoolean()) && (mc.player.isInLava() && !yLava.getValBoolean()) || mc.player.collidedHorizontally) {
            return;
        }

        if(mc.player.onGround) {
            EntityUtil.setTimer(1.15f);
            mc.player.jump();
            PlayerUtil.setSpeed(mc.player, PlayerUtil.getBaseMoveSpeed() + this.yPortSpeed);
        } else {
            mc.player.motionY = -1;
            EntityUtil.resetTimer();
        }
    }
}
