package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
//import com.kisman.cc.hud.hudmodule.ArrayList;
import com.kisman.cc.hud.hudmodule.Coord;
import com.kisman.cc.hud.hudmodule.Fps;
import com.kisman.cc.hud.hudmodule.Logo;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.LogoMode;

import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.*;

public class HUD extends Module {
	com.kisman.cc.hud.hudmodule.ArrayList arrList = new com.kisman.cc.hud.hudmodule.ArrayList();
	Coord coord = new Coord();
	Fps fps = new Fps();
	Logo logo = new Logo(Kisman.NAME, Kisman.VERSION);

	public static boolean isArrList = false;
	public static boolean isCoord = false;
	public static boolean isFps = false;
	public static boolean isLogo = false;

	public static int arrR = 0;
	public static int arrG = 0;
	public static int arrB = 0;
	public static int arrA = 0;

	public HUD() {
		super("HUD", "hud editor", Category.CLIENT);
		Kisman.instance.settingsManager.rSetting(new Setting("ArrayList", this, false));
		Kisman.instance.settingsManager.rSetting(new Setting("ArrListColor", this, "ArrayListColor", false));
		Kisman.instance.settingsManager.rSetting(new Setting("Coords", this, false));
		Kisman.instance.settingsManager.rSetting(new Setting("FPS", this, false));
		Kisman.instance.settingsManager.rSetting(new Setting("LogoLine", this, "Logo"));
		Kisman.instance.settingsManager.rSetting(new Setting("Logo", this, false));
		Kisman.instance.settingsManager.rSetting(new Setting("LogoMode", this, "Simple", new ArrayList<String>(Arrays.asList("Simple", "Best","SimpeBird", "Bird", "Kisman", "Nevis"))));
	}

	public void update() {
		boolean arrList = Kisman.instance.settingsManager.getSettingByName(this, "ArrayList").getValBoolean();
		boolean coord = Kisman.instance.settingsManager.getSettingByName(this, "Coords").getValBoolean();
		boolean fps = Kisman.instance.settingsManager.getSettingByName(this ,"FPS").getValBoolean();
		boolean logo = Kisman.instance.settingsManager.getSettingByName(this, "Logo").getValBoolean();

		String logoMode = Kisman.instance.settingsManager.getSettingByName(this, "LogoMode").getValString();

		arrR = Kisman.instance.settingsManager.getSettingByName(this, "ArrListColor").getR();
		arrG = Kisman.instance.settingsManager.getSettingByName(this, "ArrListColor").getG();
		arrB = Kisman.instance.settingsManager.getSettingByName(this, "ArrListColor").getB();
		arrA = Kisman.instance.settingsManager.getSettingByName(this, "ArrListColor").getA();
		//com.kisman.cc.hud.hudmodule.ArrayList.color[0] = Kisman.instance.settingsManager.getSettingByName(this, "ArrListColor").getColorPicker().getColor(0);
		
		if(logoMode.equalsIgnoreCase("Simple")) {
			Kisman.instance.logo.setLogoMode(LogoMode.SIMPLE);
		} else if(logoMode.equalsIgnoreCase("Best")) {
			Kisman.instance.logo.setLogoMode(LogoMode.ADVANCED);
		} else if(logoMode.equalsIgnoreCase("SimpleBird")) {
			Kisman.instance.logo.setLogoMode(LogoMode.SIMPLEBIRD);
		} else if(logoMode.equalsIgnoreCase("Bird")) {
			Kisman.instance.logo.setLogoMode(LogoMode.BIRD);
		} else if(logoMode.equalsIgnoreCase("Kisman")) {
			Kisman.instance.logo.setLogoMode(LogoMode.KISMAN);
		} else if(logoMode.equalsIgnoreCase("Nevis")) {
			Kisman.instance.logo.setLogoMode(LogoMode.NEVIS);
		}

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
