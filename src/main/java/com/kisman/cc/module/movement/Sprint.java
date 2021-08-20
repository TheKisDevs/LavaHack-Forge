package com.kisman.cc.module.movement;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.notification.ModuleMode;
import com.kisman.cc.notification.Notification;
import com.kisman.cc.notification.NotificationType;
import com.kisman.cc.notification.notifications.ModuleNotification;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Sprint extends Module {
	ModuleNotification moduleNotification;
	Notification notification;

	public Sprint() {
		super("Sprint", "i like sprinting", Category.MOVEMENT);
		moduleNotification = new ModuleNotification(ModuleMode.ENABLE, getName());
	}

	public void update() {
		mc.player.setSprinting(true);
	}

	public void onDisable() {
		mc.player.setSprinting(false);
	}
}
