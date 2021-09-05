package com.kisman.cc.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Sprint extends Module {
	public Sprint() {
		super("Sprint", "i like sprinting", Category.MOVEMENT);

		Kisman.instance.settingsManager.rSetting(new Setting("voidsetting", this, "void", "setting"));
	}

	public void update() {
		mc.player.setSprinting(true);
	}

	public void onDisable() {
		mc.player.setSprinting(false);
	}
}
