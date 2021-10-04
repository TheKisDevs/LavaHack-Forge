package com.kisman.cc.module.movement;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;

public class Sprint extends Module {
	public static Sprint instance;

	public Sprint() {
		super("Sprint", "i like sprinting", Category.MOVEMENT);

		instance = this;

		Kisman.instance.settingsManager.rSetting(new Setting("voidsetting", this, "void", "setting"));
	}

	public void update() {
		if(mc.player != null && mc.world != null) mc.player.setSprinting(true);
	}

	public void onDisable() {
		if(mc.player != null && mc.world != null) mc.player.setSprinting(false);
	}
}
