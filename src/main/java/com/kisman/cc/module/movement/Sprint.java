package com.kisman.cc.module.movement;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Sprint extends Module {
	public Sprint() {
		super("Sprint", "i like sprinting", Category.MOVEMENT);
	}

	public void update() {
		mc.player.setSprinting(true);
	}

	public void onDisable() {
		mc.player.setSprinting(false);
	}
}
