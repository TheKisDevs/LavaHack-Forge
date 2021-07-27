package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.hud.hudmodule.Logo;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HUD extends Module {
	Logo logo;

	public HUD() {
		super("HUD", "hud editor", Category.CLIENT);
		Kisman.instance.settingsManager.rSetting(new Setting("Logo", this, false));
	}
	
//	@SubscribeEvent
//	public void onRender(RenderGameOverlayEvent egoe) {
//		/*if (!egoe.type.equals(egoe.type.CROSSHAIRS)) {
//			return;
//		}*/
//		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
//		int y = 2;
//		for (Module mod : Kisman.instance.moduleManager.getModuleList()) {
//			if (!mod.getName().equalsIgnoreCase("HUD") && mod.isToggled() && mod.visible) {
//				FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
//				fr.drawString(mod.getName(), sr.getScaledWidth() - fr.getStringWidth(mod.getName()) - 1, y, 0xFFFFFF);
//				y += fr.FONT_HEIGHT;
//			}
//		}
//	}

	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent event) {
		boolean logo = Kisman.instance.settingsManager.getSettingByName(this, "Logo").getValBoolean();
		if(logo) {
			this.logo = new Logo(Kisman.NAME, Kisman.VERSION);
		}
	}


}
