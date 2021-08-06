package com.kisman.cc.notification.notifications;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.notification.ModuleMode;
import com.kisman.cc.notification.Notification;
import com.kisman.cc.notification.NotificationType;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModuleNotification extends Module {
    Notification notification;
    ModuleMode moduleMode;

    String moduleName;

    public ModuleNotification(ModuleMode moduleMode, String moduleName) {
        super("mn", "", Category.CLIENT);
        this.moduleMode = moduleMode;
        this.moduleName = moduleName;

        if(moduleMode  == ModuleMode.ENABLE) {
            notification = new Notification(NotificationType.INFO, moduleName, "Module enable!", 100, Minecraft.getMinecraft().displayWidth - 90, Minecraft.getMinecraft().displayHeight - 40, 90, 40);
        } else {
            notification = new Notification(NotificationType.INFO, moduleName, "Module disable!", 100, Minecraft.getMinecraft().displayWidth - 90, Minecraft.getMinecraft().displayHeight - 40, 90, 40);
        }
    }

    public void start() {
        setToggled(true);
    }

    public void stop() {
        setToggled(false);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent event) {
        notification.render();
    }
}
