package com.kisman.cc.features.module.movement;

import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;
import net.minecraft.network.play.client.*;

public class Spider extends Module{
	private final Setting mode = register(new Setting("Mode", this, Mode.Default));

	public Spider() {
		super("Spider", "Allows to walk on walls", Category.MOVEMENT);
		super.setDisplayInfo(() -> "[" + mode.getValString() + "]");
	}
	
	public void update() {
		if(mc.world != null && mc.player != null) {
			if(mode.checkValString(Mode.Default.name()) && !mc.player.isOnLadder() && mc.player.collidedHorizontally && mc.player.motionY < 0.2) mc.player.motionY = 0.2;
			else if(mode.checkValString(Mode.Matrix.name()) && mc.player.collidedHorizontally && mc.player.ticksExisted % 8 == 0) {
				mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
				mc.player.motionY = 0.42;
				mc.player.fallDistance = 0f;
				mc.player.connection.sendPacket(new CPacketPlayer(true));
				mc.player.motionX = 0.0;
				mc.player.motionZ = 0.0;
			}
		}
	}
	
	public enum Mode {Default, Matrix}
}
