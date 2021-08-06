package com.kisman.cc.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.notification.Notification;
import com.kisman.cc.notification.NotificationType;
import com.kisman.cc.settings.Setting;
import net.minecraft.client.Minecraft;

public class Step extends Module {
    Notification notification;

    public Step() {
        super("Step", "setting your step", Category.MOVEMENT);
        Kisman.instance.settingsManager.rSetting(new Setting("Heigth", this, 0.5f, 0.5f, 2.5f, false));
    }

    public void update() {
        float height = (float) Kisman.instance.settingsManager.getSettingByName(this, "Heigth").getValDouble();
        Minecraft.getMinecraft().player.stepHeight = height;
    }

//    public void render() {
//        notification = new com.kisman.cc.notification.Notification(NotificationType.INFO, "test title", "test message", 100, 100, 100, 90, 40);
//        notification.render();
//    }

    public void onDisable() {
        Minecraft.getMinecraft().player.stepHeight = 0.5f;
    }
}
