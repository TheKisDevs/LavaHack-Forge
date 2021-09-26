package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;

public class KillAura extends Module {
    private boolean player;
    private boolean monster;
    private boolean passive;

    private boolean hitsound;

    private double distance;

    public KillAura() {
        super("KillAura", "8", Category.COMBAT);

        Kisman.instance.settingsManager.rSetting(new Setting("HitSound", this, false));

        Kisman.instance.settingsManager.rSetting(new Setting("Targets", this, "Targets"));

        Kisman.instance.settingsManager.rSetting(new Setting("Player", this, true));
        Kisman.instance.settingsManager.rSetting(new Setting("Monster", this, true));
        Kisman.instance.settingsManager.rSetting(new Setting("Passive", this, true));

        Kisman.instance.settingsManager.rSetting(new Setting("Distance", this, "Distance"));

        Kisman.instance.settingsManager.rSetting(new Setting("Distance", this, 4.25f, 0, 4.25f, false));
    }

    public void update() {
        if(mc.player == null && mc.world == null) return;

        this.player = Kisman.instance.settingsManager.getSettingByName(this,"Player").getValBoolean();
        this.monster = Kisman.instance.settingsManager.getSettingByName(this,"Monster").getValBoolean();
        this.passive = Kisman.instance.settingsManager.getSettingByName(this,"Passive").getValBoolean();

        this.hitsound = Kisman.instance.settingsManager.getSettingByName(this,"HitSound").getValBoolean();

        this.distance = Kisman.instance.settingsManager.getSettingByName(this, "Distance").getValDouble();

        for(int i = 0; i < mc.world.loadedEntityList.size(); i++) {
            if(mc.world.loadedEntityList.get(i) != null && ((mc.world.loadedEntityList.get(i) instanceof EntityPlayer && this.player) || (mc.world.loadedEntityList.get(i) instanceof EntityMob && this.monster) || (mc.world.loadedEntityList.get(i) instanceof  EntityAnimal && this.passive))) {
                if(mc.player.getDistance(mc.world.loadedEntityList.get(i)) <= 4.15 && mc.world.loadedEntityList.get(i).ticksExisted % 20 == 0 && mc.world.loadedEntityList.get(i) != mc.player) {
                    mc.playerController.attackEntity(mc.player, mc.world.loadedEntityList.get(i));
//                    mc.player.swingArm(mc.player.swingingHand);
                    mc.player.resetCooldown();
                    if(this.hitsound) {
                        mc.player.playSound(SoundEvents.BLOCK_STONE_BREAK, 1, 1);
                    }
                }
            }
        }
    }
}
