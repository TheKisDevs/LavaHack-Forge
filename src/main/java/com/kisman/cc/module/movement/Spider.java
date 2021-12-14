package com.kisman.cc.module.movement;

import com.kisman.cc.module.*;

public class Spider extends Module{
	public Spider() {
		super("Spider", "HackCategory.PLAYER", Category.MOVEMENT);
	}
	
	public void update() {
		if(mc.world != null && mc.player != null) {
			if(!mc.player.isOnLadder()
        		&& mc.player.collidedHorizontally
        		&& mc.player.motionY < 0.2) {
				mc.player.motionY = 0.2;
        	}
		}
	}
}
