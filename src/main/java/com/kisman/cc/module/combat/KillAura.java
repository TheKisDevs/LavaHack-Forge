package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;

import java.util.ArrayList;
import java.util.Arrays;

public class KillAura extends Module {
    private boolean player;
    private boolean monster;
    private boolean passive;

    private boolean hitsound;

    private double distance;

//    private String targetMode;

    public KillAura() {
        super("KillAura", "8", Category.COMBAT);

        Kisman.instance.settingsManager.rSetting(new Setting("HitSound", this, false));

        Kisman.instance.settingsManager.rSetting(new Setting("Targets", this, "Targets"));

//        Kisman.instance.settingsManager.rSetting(new Setting("TargetMode", this, "Single", new ArrayList<>(Arrays.asList("Single", "Multy"))));

        Kisman.instance.settingsManager.rSetting(new Setting("Player", this, true));
        Kisman.instance.settingsManager.rSetting(new Setting("Monster", this, true));
        Kisman.instance.settingsManager.rSetting(new Setting("Passive", this, true));

        Kisman.instance.settingsManager.rSetting(new Setting("Distance", this, "Distance"));

        Kisman.instance.settingsManager.rSetting(new Setting("Distance", this, 4.25f, 0, 4.25f, false));
    }

    public void onEnable() {
//        this.targetMode = Kisman.instance.settingsManager.getSettingByName(this, "TargetMode").getValString();

        this.player = Kisman.instance.settingsManager.getSettingByName(this,"Player").getValBoolean();
        this.monster = Kisman.instance.settingsManager.getSettingByName(this,"Monster").getValBoolean();
        this.passive = Kisman.instance.settingsManager.getSettingByName(this,"Passive").getValBoolean();

        this.hitsound = Kisman.instance.settingsManager.getSettingByName(this,"HitSound").getValBoolean();

        this.distance = Kisman.instance.settingsManager.getSettingByName(this, "Distance").getValDouble();
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        Entity targetSingle;

//        this.targetMode = Kisman.instance.settingsManager.getSettingByName(this, "TargetMode").getValString();

        this.player = Kisman.instance.settingsManager.getSettingByName(this,"Player").getValBoolean();
        this.monster = Kisman.instance.settingsManager.getSettingByName(this,"Monster").getValBoolean();
        this.passive = Kisman.instance.settingsManager.getSettingByName(this,"Passive").getValBoolean();

        this.hitsound = Kisman.instance.settingsManager.getSettingByName(this,"HitSound").getValBoolean();

        this.distance = Kisman.instance.settingsManager.getSettingByName(this, "Distance").getValDouble();

        for (int i = 0; i < mc.world.loadedEntityList.size(); i++) {
            if (mc.world.loadedEntityList.get(i) != null && ((mc.world.loadedEntityList.get(i) instanceof EntityPlayer && this.player) || (mc.world.loadedEntityList.get(i) instanceof EntityMob && this.monster) || (mc.world.loadedEntityList.get(i) instanceof EntityAnimal && this.passive))) {
                if (mc.player.getDistance(mc.world.loadedEntityList.get(i)) <= 4.15 && mc.world.loadedEntityList.get(i).ticksExisted % 20 == 0 && mc.world.loadedEntityList.get(i) != mc.player) {
                    mc.playerController.attackEntity(mc.player, mc.world.loadedEntityList.get(i));
//                    mc.player.swingArm(mc.player.swingingHand);
                    mc.player.resetCooldown();
                    if (this.hitsound) {
                        mc.player.playSound(SoundEvents.BLOCK_STONE_BREAK, 1, 1);
                    }
                }
            }
        }
/*        if(this.targetMode.equalsIgnoreCase("Mutly")) {

        } else if(this.targetMode.equalsIgnoreCase("Single")) {
            targetSingle = getTargetSingle();
            if(targetSingle != null) {
                mc.playerController.attackEntity(mc.player, targetSingle);
                mc.player.resetCooldown();
            }
        }*/
    }

/*    private Entity getTargetSingle() {
        final float[] lowestDist = {0};
        final Entity[] lowestDistEntity = {null};

        mc.world.loadedEntityList.stream()
                .filter(entity -> entity != null)
                .filter(entity -> entity != mc.player)
                .filter(entity ->
                                (entity instanceof EntityPlayer && this.player) ||
                                (entity instanceof  EntityMob && this.monster) ||
                                (entity instanceof EntityAnimal && this.passive)
                )
                .filter(entity -> mc.player.getDistance(entity) < 4.26)
                .forEach(entity -> {
                    if(lowestDistEntity[0] == null) {
                        lowestDistEntity[0] = entity;
                    } else {
                        if(lowestDist[0] == 0) {
                            lowestDist[0] = mc.player.getDistance(entity);
                        } else if(lowestDist[0] < entity.getDistance(mc.player)) {
                            lowestDist[0] = mc.player.getDistance(entity);
                        }
                    }
        });

        return lowestDistEntity[0];
    }*/
}
