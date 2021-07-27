package com.kisman.cc.module.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemExpBottle;
import net.minecraft.item.ItemSword;

public class Criticals extends Module {
    public Criticals() {
        super("Criticals", "all criticals hit", Category.COMBAT);
        Kisman.instance.settingsManager.rSetting(new Setting("OnlySword", this, false));
    }

    public void update() {
        Item main = mc.player.getHeldItemMainhand().getItem();
        boolean mainSword = main instanceof ItemSword;
        boolean onlySword = Kisman.instance.settingsManager.getSettingByName(this, "OnlySword").getValBoolean();
        if(onlySword == false) {
            mc.player.onCriticalHit(mc.player.getAttackingEntity());
        } else if(onlySword == true) {
            if(mainSword)
            mc.player.onCriticalHit(mc.player.getAttackingEntity());
        }
    }
}
