package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.*;
import com.kisman.cc.oldclickgui.csgo.components.Slider;
import com.kisman.cc.settings.Setting;

import com.kisman.cc.util.customfont.CustomFontUtil;

import java.util.*;

public class HUD extends Module {
	public static HUD instance;

	public Setting astolfoColor = new Setting("Astolfo Color", this, false);
	public Setting offsets = new Setting("Offsets", this, 2, 0, 10, true);
	public Setting glow = new Setting("Glow", this, false);
	public Setting glowOffset = new Setting("Glow Offset", this, 6, 0, 20, true);
	public Setting glowAlpha = new Setting("Glow Alpha", this, 255, 0, 255, true);
	public Setting background = new Setting("Background", this, false);
	public Setting bgAlpha = new Setting("Bg Alpha", this, 255, 0, 255, true);

	private final Setting arrLine = new Setting("ArrLine", this, "ArrayList");
	public Setting arrMode = new Setting("ArrayList Mode", this, "RIGHT", new ArrayList<>(Arrays.asList("LEFT", "RIGHT")));
	public Setting arrY = new Setting("ArrayList Y", this, 150, 0, mc.displayHeight, true);
	public Setting arrColor = new Setting("ArrayList Color", this, "Color", new float[] {3f, 0.03f, 0.33f, 1f}, false);
	public Setting arrGradient = new Setting("Array Gradient", this, Gradient.None);
	public Setting arrGradientDiff = new Setting("Array Gradient Diff", this, 200, 0, 1000, Slider.NumberType.TIME);

	private final Setting welLine = new Setting("WelLine", this, "Welcomer");
	public Setting welColor = new Setting("WelColor", this, "WelcomerColor", new float[] {3f, 0.03f, 0.33f, 1f}, false);

	private final Setting pvpLine = new Setting("PvpLine", this, "PvpInfo");
	public Setting pvpY = new Setting("PvpInfo Y", this, 200, 0, mc.displayHeight, true);

	private final Setting armLine = new Setting("ArmLine", this, "Armor");
	public Setting armExtra = new Setting("Extra Info", this, false);
	public Setting armDmg = new Setting("Damage", this, false);

	private final Setting radarLine = new Setting("RadarLine", this, "Radar");
	public Setting radarDist = new Setting("Max Distance", this, 50, 10, 50, true);
	public Setting radarY = new Setting("Radar Y", this, 3 + (CustomFontUtil.getFontHeight() * 2), 0, mc.displayHeight, true);

	private final Setting speedLine = new Setting("SpeedLine", this, "Speed");
	public Setting speedMode = new Setting("Speed Mode", this, "km/h", new ArrayList<>(Arrays.asList("b/s", "km/h")));

	private final Setting logoLine = new Setting("LogoLine", this, "Logo");
	public Setting logoMode = new Setting("Logo Mode", this, LogoMode.Simple);
	public Setting logoGlow = new Setting("Glow", this, false);
	public Setting logoBold = new Setting("Name Bold", this, false);

	private Setting indicLine = new Setting("IndicLine", this, "Indicators");
	public Setting indicY = new Setting("Indicators Y", this, 20, 0, mc.displayHeight, true);
	public Setting indicThemeMode = new Setting("Indicators Theme", this, IndicatorsThemeMode.Default);
	public Setting indicShadowSliders = new Setting("Indicators Shadow Sliders", this, false);

	private Setting thudLine = new Setting("ThudLine", this, "TargetHud");
	public Setting thudTheme = new Setting("TargetHud Theme", this, TargetHudThemeMode.Vega);
	public Setting thudShadowSliders = new Setting("TargetHud Shadow Sliders", this, false);

	public HUD() {
		super("HudEditor", "hud editor", Category.CLIENT);

		instance = this;

		setmgr.rSetting(astolfoColor);
		setmgr.rSetting(offsets);
		setmgr.rSetting(glow);
		setmgr.rSetting(glowOffset);
		setmgr.rSetting(glowAlpha);
		setmgr.rSetting(background);
		setmgr.rSetting(bgAlpha);

		setmgr.rSetting(arrLine);
		setmgr.rSetting(arrMode);
		setmgr.rSetting(arrY);
		setmgr.rSetting(arrColor);
		setmgr.rSetting(arrGradient);
		setmgr.rSetting(arrGradientDiff);

		setmgr.rSetting(welLine);
		setmgr.rSetting(welColor);

		setmgr.rSetting(pvpLine);
		setmgr.rSetting(pvpY);

		setmgr.rSetting(armLine);
		setmgr.rSetting(armExtra);
		setmgr.rSetting(armDmg);

		setmgr.rSetting(radarLine);
		setmgr.rSetting(radarDist);
		setmgr.rSetting(radarY);

		setmgr.rSetting(speedLine);
		setmgr.rSetting(speedMode);

		setmgr.rSetting(logoLine);
		setmgr.rSetting(logoMode);
		setmgr.rSetting(logoGlow);
		setmgr.rSetting(logoBold);

		setmgr.rSetting(indicLine);
		setmgr.rSetting(indicY);
		setmgr.rSetting(indicThemeMode);
		setmgr.rSetting(indicShadowSliders);

		setmgr.rSetting(thudLine);
		setmgr.rSetting(thudTheme);
		setmgr.rSetting(thudShadowSliders);
	}

	public void onEnable() {
        mc.displayGuiScreen(Kisman.instance.hudGui);
		super.setToggled(false);
	}

	public enum LogoMode {Simple, CSGO}
	public enum Gradient {None, Simple, Sideway, Astolfo}
	public enum IndicatorsThemeMode {Default, Rewrite}
	public enum TargetHudThemeMode {Vega, Rewrite}
}
