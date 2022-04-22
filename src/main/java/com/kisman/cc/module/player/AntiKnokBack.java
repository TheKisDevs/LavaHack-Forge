package com.kisman.cc.module.player;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiKnokBack extends Module {
	public AntiKnokBack() {
		super("AntiKnockBack", "i hate being knocked back", Category.PLAYER);
		Kisman.instance.settingsManager.rSetting(new Setting("Horizontal", this, 90, 0, 100, true));
		Kisman.instance.settingsManager.rSetting(new Setting("Vertical", this, 100, 0, 100, true));
	}
	
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent e) {
		if(mc.player == null || mc.world == null) return;

		float horizontal = (float) Kisman.instance.settingsManager.getSettingByName(this, "Horizontal").getValDouble();
		float vertical = (float) Kisman.instance.settingsManager.getSettingByName(this, "Vertical").getValDouble();

		if (mc.player.hurtTime == mc.player.maxHurtTime && mc.player.maxHurtTime > 0) {
			mc.player.motionX *= horizontal / 100;
			mc.player.motionY *= vertical / 100;
			mc.player.motionZ *= horizontal / 100;
		}
	}
}
