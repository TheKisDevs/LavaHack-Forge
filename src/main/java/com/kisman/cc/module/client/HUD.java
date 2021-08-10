package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.hud.hudmodule.ArrayList;
import com.kisman.cc.hud.hudmodule.Coord;
import com.kisman.cc.hud.hudmodule.Fps;
import com.kisman.cc.hud.hudmodule.Logo;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HUD extends Module {
	ArrayList arrList = new ArrayList();
	Coord coord = new Coord();
	Fps fps = new Fps();
	Logo logo = new Logo(Kisman.NAME, Kisman.VERSION);

	public static boolean isArrList = false;
	public static boolean isCoord = false;
	public static boolean isFps = false;
	public static boolean isLogo = false;

	public HUD() {
		super("HUD", "hud editor", Category.CLIENT);
		Kisman.instance.settingsManager.rSetting(new Setting("ArrayList", this, false));
		Kisman.instance.settingsManager.rSetting(new Setting("Coords", this, false));
		Kisman.instance.settingsManager.rSetting(new Setting("FPS", this, false));
		Kisman.instance.settingsManager.rSetting(new Setting("Logo", this, false));
	}

	public void update() {
		boolean arrList = Kisman.instance.settingsManager.getSettingByName(this, "ArrayList").getValBoolean();
		boolean coord = Kisman.instance.settingsManager.getSettingByName(this, "Coords").getValBoolean();
		boolean fps = Kisman.instance.settingsManager.getSettingByName(this ,"FPS").getValBoolean();
		boolean logo = Kisman.instance.settingsManager.getSettingByName(this, "Logo").getValBoolean();
		if(arrList) {
			isArrList = true;
		} else {
			isArrList = false;
		}
		if(logo) {
			isLogo = true;
		} else {
			isLogo = false;
		}
		if(fps) {
			isFps = true;
		} else {
			isFps = false;
		}
		if(coord) {
			isCoord = true;
		} else {
			isCoord = false;
		}
	}
}
