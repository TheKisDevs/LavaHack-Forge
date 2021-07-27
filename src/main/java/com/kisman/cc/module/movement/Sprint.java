package com.kisman.cc.module.movement;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Sprint extends Module {

	public Sprint() {
		super("Sprint", "i like sprinting", Category.MOVEMENT);
	}
	
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent e) {
		KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
	}
}
