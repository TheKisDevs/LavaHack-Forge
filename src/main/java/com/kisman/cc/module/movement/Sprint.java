package com.kisman.cc.module.movement;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import net.minecraft.client.settings.KeyBinding;

public class Sprint extends Module {

	public Sprint() {
		super("Sprint", "i like sprinting", Category.MOVEMENT);
	}

	public void render() {
		KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
	}

	public void onDisable() {
		KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
	}
}
