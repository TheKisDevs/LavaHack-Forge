package com.kisman.cc.module.movement;

import java.util.*;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventPlayerUpdate;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.*;

import com.kisman.cc.util.*;
import i.gishreloaded.gishcode.utils.Utils;
import i.gishreloaded.gishcode.wrappers.Wrapper;
import me.zero.alpine.listener.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

public class Speed extends Module {
    public static Speed instance;

    private float yPortSpeed;

    public Setting speedMode = new Setting("SpeedMode", this, "Strafe", new ArrayList<>(Arrays.asList("Strafe", "YPort", "Sti")));

    private Setting yPortLine = new Setting("YPortLine", this, "YPort");
    private Setting yWater = new Setting("Water", this, false);
    private Setting yLava = new Setting("Lava", this, false);

    private Setting stiLine = new Setting("StiLine", this, "Sti");
    private Setting stiSpeed = new Setting("StiSpeed", this, 4, 0.1, 10, true);

    private float ovverideSpeed = 1;

    public Speed() {
        super("Speed", "SPID", Category.MOVEMENT);

        instance = this;

        setmgr.rSetting(speedMode);

        setmgr.rSetting(yPortLine);
        Kisman.instance.settingsManager.rSetting(new Setting("YPortSpeed", this, 0.06f, 0.01f, 0.15f, false));
        setmgr.rSetting(yWater);
        setmgr.rSetting(yLava);

        setmgr.rSetting(stiLine);
        setmgr.rSetting(stiSpeed);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);

        EntityUtil.resetTimer();

        mc.timer.tickLength = 50;
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        super.setDisplayInfo("[" + speedMode.getValString() + TextFormatting.GRAY + "]");

        boolean boost = Math.abs(mc.player.rotationYawHead - Wrapper.INSTANCE.player().rotationYaw) < 90;

        this.yPortSpeed = (float) Kisman.instance.settingsManager.getSettingByName(this, "YPortSpeed").getValDouble();

        if(mc.player.moveForward > 0 && mc.player.hurtTime < 5 && speedMode.getValString().equalsIgnoreCase("Strafe")) {
            if(mc.player.onGround) {
                mc.player.motionY = 0.405;
                float f = Utils.getDirection();

                mc.player.motionX -= (MathHelper.sin(f) * 0.2F);
                mc.player.motionZ += (MathHelper.cos(f) * 0.2F);
            } else {
                double currentSpeed = Math.sqrt(mc.player.motionX * mc.player.motionX + mc.player.motionZ * mc.player.motionZ);
                double speed = boost ? 1.0064 : 1.001;
  
                double direction = Utils.getDirection();

                mc.player.motionX = -Math.sin(direction) * speed * currentSpeed;
                mc.player.motionZ = Math.cos(direction) * speed * currentSpeed;
            }
        }

        if(speedMode.getValString().equalsIgnoreCase("YPort")) {
            doYPortSpeed();
        }
    }

    private void doYPortSpeed() {
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

    @EventHandler
    private final Listener<EventPlayerUpdate> listener = new Listener<>(event -> {
        if(speedMode.getValString().equalsIgnoreCase("Sti")) {
            if(ovverideSpeed != 1 && ovverideSpeed > 1) {
                mc.timer.tickLength = 50 / ovverideSpeed;
                return;
            }

            mc.timer.tickLength = 50 / getSpeed();
        }
    });

    private float getSpeed() {
        return Math.max((float) stiSpeed.getValDouble(), 0.1f);
    }
}
