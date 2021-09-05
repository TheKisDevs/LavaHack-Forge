package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
//import com.kisman.cc.hud.hudmodule.ArrayList;
// import com.kisman.cc.hud.hudmodule.Coord;
// import com.kisman.cc.hud.hudmodule.Fps;
// import com.kisman.cc.hud.hudmodule.Logo;
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

	public static int arrR = 0;
	public static int arrG = 0;
	public static int arrB = 0;
	public static int arrA = 0;

	public HUD() {
		super("HUD", "hud editor", Category.CLIENT);
		Kisman.instance.settingsManager.rSetting(new Setting("ArrayList", this, false));
		Kisman.instance.settingsManager.rSetting(new Setting("ArrListColor", this, "ArrayListColor", new float[] {3f, 0.03f, 0.33f, 1f}, false));
		Kisman.instance.settingsManager.rSetting(new Setting("LogoLine", this, "Logo"));
		Kisman.instance.settingsManager.rSetting(new Setting("LogoMode", this, "Simple", new ArrayList<String>(Arrays.asList("Simple", "Best","SimpeBird", "Bird", "Kisman", "Nevis"))));
	}

	public void update() {
		arrR = Kisman.instance.settingsManager.getSettingByName(this, "ArrayListColor").getR();
		arrG = Kisman.instance.settingsManager.getSettingByName(this, "ArrayListColor").getG();
		arrB = Kisman.instance.settingsManager.getSettingByName(this, "ArrayListColor").getB();
		arrA = Kisman.instance.settingsManager.getSettingByName(this, "ArrayListColor").getA();
	}

	public void onEnable() {
		super.onEnable();
        mc.displayGuiScreen(Kisman.instance.hudGui);
		super.setToggled(false);
	}
}
