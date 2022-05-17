package com.kisman.cc.hud;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import net.minecraft.client.Minecraft;

public class HudModule extends Module {
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

	public double getX() {return x;}
	public void setX(double x) {this.x = x;}
	public double getY() {return y;}
	public void setY(double y) {this.y = y;}
	public double getW() {return w;}
	public void setW(double w) {this.w = w;}
	public double getH() {return h;}
	public void setH(double h) {this.h = h;}
}