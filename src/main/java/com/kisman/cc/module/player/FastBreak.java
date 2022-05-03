package com.kisman.cc.module.player;

import com.kisman.cc.module.*;
import com.kisman.cc.util.BlockUtil2;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FastBreak extends Module{
    public FastBreak() {
        super("FastBreak", "fast++", Category.PLAYER);
    }

    public void update() {
        if(mc.playerController == null) return;
        mc.playerController.blockHitDelay = 0;
    }

    @SubscribeEvent
	public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event){
        float progress = mc.playerController.curBlockDamageMP + BlockUtil2.getHardness(event.getPos());
    	if(progress >= 1) return;
    	mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getPos(), mc.objectMouseOver.sideHit));
	}
}
