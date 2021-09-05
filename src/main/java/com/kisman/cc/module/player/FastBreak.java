package com.kisman.cc.module.player;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;

import com.kisman.cc.settings.Setting;
import i.gishreloaded.gishcode.wrappers.Wrapper;
import i.gishreloaded.gishcode.utils.*;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FastBreak extends Module{
    public FastBreak() {
        super("FastBreak", "fast++", Category.PLAYER);

        Kisman.instance.settingsManager.rSetting(new Setting("voidsetting", this, "void", "setting"));
    }

    public void update() {
        PlayerControllerUtils.setBlockHitDelay(0);
    }

    @SubscribeEvent
	public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event){
        float progress = PlayerControllerUtils.getCurBlockDamageMP() + BlockUtils.getHardness(event.getPos());	
    	if(progress >= 1) return;
    	Wrapper.INSTANCE.sendPacket(new CPacketPlayerDigging(
    			CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getPos(),
    			Wrapper.INSTANCE.mc().objectMouseOver.sideHit));
	}
}
