package com.kisman.cc.hud.hudmodule;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class HudModule {
	protected static Minecraft mc = Minecraft.getMinecraft();

	private String name, description;
	private int key;
	private HudCategory category;
	private boolean toggled;
	public boolean visible = true;
	public boolean drag = false;
	private double x = 0, y = 0, w = 0, h = 0;

	public HudModule(String name, String description, HudCategory category) {
		this.name = name;
		this.description = description;
		this.key = 0;
		this.category = category;
		this.toggled = false;
	}

	public HudModule(String name, HudCategory category) {
		this(name, "", category);
	}

	public HudModule(String name, HudCategory category, boolean drag) {
		this(name, category);
		this.drag = drag;
	}

	public void setToggled(boolean toggled) {
		this.toggled = toggled;
		if (this.toggled) this.onEnable();
		else this.onDisable();
	}

	public void toggle() {
		this.toggled = !this.toggled;
		if (this.toggled) this.onEnable();
		else this.onDisable();
	}

	public String getDescription() {return description;}
	public void setDescription(String description) {this.description = description;}
	public int getKey() {return key;}
	public void setKey(int key) {this.key = key;}
	public boolean isToggled() {return toggled;}
	public void onEnable() {MinecraftForge.EVENT_BUS.register(this);}
	public void onDisable() {MinecraftForge.EVENT_BUS.unregister(this);}
	public String getName() {return this.name;}
	public HudCategory getCategory() {return this.category;}
	public void update(){}
	public void render(){}
	public double getX() {return x;}
	public void setX(double x) {this.x = x;}
	public double getY() {return y;}
	public void setY(double y) {this.y = y;}
	public double getW() {return w;}
	public void setW(double w) {this.w = w;}
	public double getH() {return h;}
	public void setH(double h) {this.h = h;}
}