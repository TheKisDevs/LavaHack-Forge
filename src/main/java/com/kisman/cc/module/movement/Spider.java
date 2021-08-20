package com.kisman.cc.module.movement;

import java.lang.reflect.Field;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;

import i.gishreloaded.gishcode.utils.system.Mapping;
import i.gishreloaded.gishcode.wrappers.Wrapper;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class Spider extends Module{
	
	public Spider() {
		super("Spider", "HackCategory.PLAYER", Category.MOVEMENT);
	}
	
	public void update() {
		if(!(mc.world == null || mc.player == null)) {
			if(!Wrapper.INSTANCE.player().isOnLadder() 
        		&& Wrapper.INSTANCE.player().collidedHorizontally 
        		&& Wrapper.INSTANCE.player().motionY < 0.2) {
        		Wrapper.INSTANCE.player().motionY = 0.2;
        	}
		}
	}
}
