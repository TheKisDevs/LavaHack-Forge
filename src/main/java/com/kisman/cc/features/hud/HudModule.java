package com.kisman.cc.features.hud;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.gui.api.Draggable;
import net.minecraft.client.Minecraft;

public class HudModule extends Module implements Draggable {
	protected static Minecraft mc = Minecraft.getMinecraft();

	public boolean drag = false;
	private double x = 0, y = 0, w = 0, h = 0;

	public HudModule(String name, String description) {
		super(name, description, Category.RENDER);
	}

	public HudModule(String name, String description, boolean drag) {
		this(name, description);
		this.drag = drag;
	}

	public HudModule(String name) {
		this(name, "");
	}

	public HudModule(String name, boolean drag) {
		this(name);
		this.drag = drag;
	}

	@Override public double getX() {return x;}
	@Override public void setX(double x) {this.x = x;}
	@Override public double getY() {return y;}
	@Override public void setY(double y) {this.y = y;}
	@Override public double getW() {return w;}
	@Override public void setW(double w) {this.w = w;}
	@Override public double getH() {return h;}
	@Override public void setH(double h) {this.h = h;}
}