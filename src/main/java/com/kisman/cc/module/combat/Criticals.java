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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;

public class Criticals extends Module {
    public Criticals() {
        super("Criticals", "all criticals hit", Category.COMBAT);
        ArrayList<String> options = new ArrayList<>();
        options.add("MiniJump");
        options.add("Vanilla");
        Kisman.instance.settingsManager.rSetting(new Setting("Mode:", this, "MiniJump", options));
        Kisman.instance.settingsManager.rSetting(new Setting("OnlySword", this, false));
    }

    @SubscribeEvent
    public void update(TickEvent.ClientTickEvent event) {
        Item main = mc.player.getHeldItemMainhand().getItem();
        boolean mainSword = main instanceof ItemSword;
        boolean onlySword = Kisman.instance.settingsManager.getSettingByName(this, "OnlySword").getValBoolean();
        if(onlySword == false) {
            mc.player.jump();
            mc.player.motionY -= .30000001192092879;
        } else if(onlySword == true) {
            if(mainSword) {
                mc.player.jump();
                mc.player.motionY -= .30000001192092879;
            }
        }
    }
}
