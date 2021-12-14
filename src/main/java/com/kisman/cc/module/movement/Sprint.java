package com.kisman.cc.module.movement;

import com.kisman.cc.module.*;

public class Sprint extends Module {
	public static Sprint instance;

	public Sprint() {
		super("Sprint", "i like sprinting", Category.MOVEMENT);

		instance = this;
	}

	public void update() {
		if(mc.player != null && mc.world != null) mc.player.setSprinting(true);
	}

	public void onDisable() {
		if(mc.player != null && mc.world != null) mc.player.setSprinting(false);
	}
}
