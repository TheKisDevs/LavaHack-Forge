package com.kisman.cc.features.hud;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.gui.api.Draggable;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.settings.util.ColorPattern;
import com.kisman.cc.util.enums.LinkedPlaces;

public class HudModule extends Module implements Draggable {
	public final Setting shaderSetting = new Setting("Shader", this, false);

	public final SettingEnum<LinkedPlaces> placeSetting = new SettingEnum<>("Linked Place", this, LinkedPlaces.None).setTitle("Place").onChange0(setting -> {
		setting.getValEnum().move(this);

		return null;
	});

	public boolean drag = false;
	private double x = 0, y = 0, w = 0, h = 0;

	public LinkedPlaces place = LinkedPlaces.None;

	public HudModule(String name, String description) {
		super(name, description, Category.RENDER);
	}

	public HudModule(String name, String description, boolean drag) {
		this(name, description);
		this.drag = drag;

		place.add(this);
	}

	public HudModule(String name) {
		this(name, "");
	}

	public HudModule(String name, boolean drag) {
		this(name);
		this.drag = drag;
	}

	@Override
	public void onEnable() {
		super.onEnable();
		placeSetting.getValEnum().move(this);
	}

	@Override
	public void onDisable() {
		super.onDisable();
		LinkedPlaces.None.move(this);
	}

	@Override public double getX() {return x;}
	@Override public void setX(double x) {this.x = x;}
	@Override public double getY() {return y;}
	@Override public void setY(double y) {this.y = y;}
	@Override public double getW() {return w;}
	@Override public void setW(double w) {this.w = w;}
	@Override public double getH() {return h;}
	@Override public void setH(double h) {this.h = h;}

	protected ColorPattern colors() {
		return new ColorPattern(this).preInit().init();
	}
}