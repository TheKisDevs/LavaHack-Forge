package com.kisman.cc.module.misc;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoDisconnect extends Module {
    public AutoDisconnect() {
        super("AutoDisconnect", "", Category.CHAT);
        Kisman.instance.settingsManager.rSetting(new Setting("HP", this, 10, 1, 20, true));
    }

    public void update() {
//        int hp = (int) Kisman.instance.settingsManager.getSettingByName(this, "HP").getValDouble();
//        if(mc.player.getHealth() <= hp) {
//            mc.
//        }
    }
}
